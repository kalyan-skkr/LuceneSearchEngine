import json
import jsonpickle

from flask import Flask, request, jsonify
import bs4 as bs
import re
import nltk
import lxml
from gensim.models import Word2Vec, KeyedVectors, Doc2Vec

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
# %matplotlib inline

from collections import namedtuple
import gensim.utils
from langdetect import detect
import re
import string

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
        v1 = word2vec.most_similar(query)
    except Exception:
        return jsonify([])

    v2 = []
    for x in v1:
        word = Words(x[0], x[1])
        v2.append(word)

    return jsonpickle.encode(v2)


def create_model():
    with open("/Users/kalyansabbella/Documents/Test/Doc/dblp.xml", "r") as file:
        content = file.readlines()
        content = " ".join(content)

    parsed_article = bs.BeautifulSoup(content, 'lxml')

    paragraphs = parsed_article.find_all('title')
    find_words(paragraphs)


def fetch_model():
    return KeyedVectors.load_word2vec_format("/Users/kalyansabbella/Documents/Test/w2c/w2v_model.bin", binary=True)


def find_words(paragraphs):
    article_text = ""

    for p in paragraphs:
        article_text += p.text

    # Cleaning the text
    processed_article = article_text.lower()
    processed_article = re.sub('[^a-zA-Z]', ' ', processed_article)
    processed_article = re.sub(r'\s+', ' ', processed_article)

    # Preparing the dataset
    all_sentences = nltk.sent_tokenize(processed_article)

    all_words = [nltk.word_tokenize(sent) for sent in all_sentences]

    # Removing Stop Words
    from nltk.corpus import stopwords
    for i in range(len(all_words)):
        all_words[i] = [w for w in all_words[i] if w not in stopwords.words('english')]

    word2vec = Word2Vec(all_words, min_count=2)
    word2vec.wv.save_word2vec_format("/Users/kalyansabbella/Documents/Test/w2c/w2v_model.bin", binary=True)


@app.route('/doc2ec', methods=['GET'])
def doc2vec():
    imdbdata = pd.read_csv('/Users/kalyansabbella/Downloads/archive/movies_metadata.csv')
    data = np.array(imdbdata.overview)
    titles = np.array(imdbdata.original_title)

    imdbdata.head()

    SentimentDocument = namedtuple('SentimentDocument', 'words tags title original_number')
    n = 0
    alldocs = []  # Will hold all docs in original order

    regex = re.compile('[%s]' % re.escape(string.punctuation))  # to remove punctuation

    for line_no, line in enumerate(data):
        if type(line) == str:
            if len(line) > 150:
                if detect(line) == 'en':
                    print(line)
                    line = regex.sub('', line)
                    tokens = gensim.utils.to_unicode(line).lower().split()
                    words = tokens[0:]
                    tags = [n]
                    title = titles[line_no]
                    alldocs.append(SentimentDocument(words, tags, title, line_no))
                    n = n + 1

    l = []
    for doc in alldocs:
        l.append(len(doc.words))

    print('Number of Documents : ', len(alldocs))
    print('Mean length of documents : ', np.mean(l))

    plt.figure(figsize=(20, 6))
    plt.bar(range(0, len(l)), l)
    plt.xlabel('Documents')
    plt.ylabel('Length of the description')

    index = 0
    doc = alldocs[index]
    print(doc, '\n')
    print(data[doc.original_number])

    # PV-DM
    model = Doc2Vec(dm=1, size=300, window=10, hs=0, min_count=10, dbow_words=1, sample=1e-5)

    # build the vocabulary
    model.build_vocab(alldocs)

    model.train(alldocs, total_examples=model.corpus_count, epochs=100, start_alpha=0.01, end_alpha=0.01)


    model.voca
    # model.save("model")
    # Doc2Vec.load("model")

    model.wv.most_similar_cosmul(negative=["man"], positive=["king", "woman"])

    model.wv.most_similar_cosmul(positive=["love"])

    tokens = "love"

    new_vector = model.infer_vector(tokens.split(), alpha=0.001, steps=5)
    sims = model.docvecs.most_similar([new_vector], topn=model.docvecs.co)  # get *all* similar documents

    print("Most : ", data[alldocs[sims[0][0]].original_number], "\n")
    print("Median : ", data[alldocs[sims[17000][0]].original_number], "\n")
    print("Least : ", data[alldocs[sims[-1][0]].original_number])


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
