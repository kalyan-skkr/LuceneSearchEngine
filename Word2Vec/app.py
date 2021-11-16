import json
import jsonpickle

from flask import Flask, request, jsonify
import bs4 as bs
import re
import nltk
import lxml
from gensim.models import Word2Vec, KeyedVectors, Doc2Vec
from nltk.corpus import words, stopwords

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
# %matplotlib inline

from collections import namedtuple
import gensim.utils
from langdetect import detect
import re
import string
import cython

app = Flask(__name__)


class Words:
    def __init__(self, key, score):
        self.key = key
        self.score = score


@app.route('/word2vec', methods=['GET'])
def main():
    # create_model()
    word2vec = fetch_model()
    # FindSimilarWords

    if 'query' in request.args:
        query = request.args['query']
    else:
        return jsonify([])

    try:
        v1 = word2vec.most_similar(positive=[query], topn=1000)
    except Exception as e:
        return jsonify([])

    v2 = []
    count = 0

    for x in v1:
        if count >= 15:
            break
        if x[0] in words.words() and x[0] not in stopwords.words('english'):
            word = Words(x[0], x[1])
            v2.append(word)
            count += 1

    return jsonpickle.encode(v2[3:])


def create_model():
    with open("/Users/kalyansabbella/Documents/Test/dblptitles.txt", "r") as file:
        content = file.readlines()
    find_words(content)


def find_words(paragraphs):
    paragraphs = [each_title.lower() for each_title in paragraphs]
    paragraphs = [re.sub('[^a-zA-Z]', ' ', each_title) for each_title in paragraphs]
    paragraphs = [re.sub(r'\s+', ' ', each_title) for each_title in paragraphs]

    processed_article = ''.join(paragraphs)

    # Preparing the dataset
    all_sentences = nltk.sent_tokenize(processed_article)

    all_words = [nltk.word_tokenize(sent) for sent in all_sentences]

    word2vec = Word2Vec(all_words, vector_size=300, min_count=5, workers=4)
    word2vec.wv.save_word2vec_format("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/w2v_model.bin",
                                     binary=True)


def fetch_model():
    # return KeyedVectors.load_word2vec_format("/Users/kalyansabbella/Documents/Test/w2c/w2v1_model.bin", binary=True)
    return KeyedVectors.load_word2vec_format(
        "/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/w2v_model.bin", binary=True)


@app.route('/doc2vec', methods=['GET'])
def doc2vec():
    with open("/Users/kalyansabbella/Documents/Test/titles.txt", "r") as file:
        content = file.readlines()
    if 'query' in request.args:
        query = request.args['query']
    else:
        return jsonify([])

    paragraphs = [each_title.lower() for each_title in content]
    paragraphs = [re.sub('[^a-zA-Z]', ' ', each_title) for each_title in paragraphs]
    paragraphs = [re.sub(r'\s+', ' ', each_title) for each_title in paragraphs]
    train_corpus = []

    for i, p in enumerate(paragraphs):
        tokens = gensim.utils.simple_preprocess(p)
        train_corpus.append(gensim.models.doc2vec.TaggedDocument(tokens, [i]))

    model = gensim.models.doc2vec.Doc2Vec(vector_size=300, min_count=2, epochs=40)
    model.build_vocab(train_corpus)
    model.train(train_corpus, total_examples=model.corpus_count, epochs=model.epochs)
    query_token = query.split()
    inferred_vector = model.infer_vector(query_token)
    sims = model.dv.most_similar([inferred_vector], topn=len(model.dv))
    doc_titles = []
    for i in range(3):
        idx, cos_sim = sims[i]
        doc_titles.append(" ".join(train_corpus[idx].words))
    return doc_titles


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002)
