import pickle

import jsonpickle
from flask import Flask, request, jsonify
import nltk
from nltk.corpus import words
import lxml
from gensim.models import Word2Vec, KeyedVectors, Doc2Vec
from nltk.corpus import words, stopwords
import gensim.utils
import re
from gensim.parsing.preprocessing import remove_stopwords, preprocess_string
from gensim.test.utils import get_tmpfile

app = Flask(__name__)


def create_doc2vec_model():
    content = get_content()
    train_corpus = gettraincorpus(content=content, flag_d2v=True)
    model = gensim.models.doc2vec.Doc2Vec(vector_size=150, min_count=1, epochs=25)
    model.build_vocab(train_corpus)
    model.train(train_corpus, total_examples=model.corpus_count, epochs=model.epochs)
    savemodel("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Doc2Vec/doc2vec_model", model)


def fetch_doc2vec_model():
    fname = get_tmpfile("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Doc2Vec/doc2vec_model")
    return Doc2Vec.load(fname)


def get_content():
    with open("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/dblp_title.txt", "r") as file:
        content = file.readlines()
    content = [remove_stopwords(each_title) for each_title in content]
    return content


def gettraincorpus(content=[], flag_w2v=False, flag_d2v=False, flag_new=True):
    corpus = []
    if flag_d2v:
        fileName = '/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Doc2Vec/TrainCorpus/corpus.pkl'
    if flag_w2v:
        fileName = '/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Word2Vec/TrainCorpus/corpus.pkl'

    if flag_new:
        for i, p in enumerate(content):
            if p != 'Home Page\n':
                tokens = gensim.utils.simple_preprocess(p)
                if flag_d2v:
                    corpus.append(gensim.models.doc2vec.TaggedDocument(tokens, [i]))
                if flag_w2v:
                    corpus.append(tokens)
        with open(fileName, 'wb') as f:
            pickle.dump(corpus, f)

    else:
        with open(fileName, 'rb') as f:
            train_corpus = pickle.load(f)

    return train_corpus


def create_word2vec_model():
    content = get_content()
    train_corpus = gettraincorpus(content=content, flag_w2v=True)
    model = Word2Vec(train_corpus, vector_size=150, min_count=1, workers=10, epochs=25)
    savemodel("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Word2Vec/word2vec_model", model)


def fetch_word2vec_model():
    fname = get_tmpfile("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Word2Vec/word2vec_model")
    return Word2Vec.load(fname)


def savemodel(filename, model):
    fname = get_tmpfile(filename)
    model.save(fname)


d2v_trainCorpus = gettraincorpus(flag_new=False, flag_d2v=True)
d2v_model = fetch_doc2vec_model()

w2v_model = fetch_word2vec_model()


class Words:
    def __init__(self, key, score):
        self.key = key
        self.score = score


class Docs:
    def __init__(self, key, score):
        self.key = key
        self.score = score


@app.route('/word2vec', methods=['GET'])
def main():
    try:
        # create_word2vec_model()
        # word2vec = fetch_word2vec_model()
        word2vec = w2v_model

        if 'query' in request.args:
            query = request.args['query']
        else:
            return jsonify([])

        queryList = query.split()
        v1 = word2vec.wv.most_similar(positive=queryList, topn=10)

        similarWords = []

        for x in v1:
            if x[0] not in queryList:
                word = Words(x[0], x[1])
                similarWords.append(word)
        return jsonpickle.encode(similarWords)
    except Exception as e:
        return jsonify([])


@app.route('/doc2vec', methods=['GET'])
def doc2vec():
    try:
        # create_doc2vec_model()
        # doc2vec = fetch_doc2vec_model()
        doc2vec = d2v_model

        if 'query' in request.args:
            query = request.args['query']
        else:
            return jsonify([])

        # train_corpus = gettraincorpus(newFlag=False)
        train_corpus = d2v_trainCorpus

        query_token = query.split()
        inferred_vector = doc2vec.infer_vector(query_token)
        sims = doc2vec.dv.most_similar([inferred_vector], topn=20)
        documents = []
        count = 0
        for i in range(len(sims)):
            if count == 5:
                break
            idx, cos_sim = sims[i]
            if idx <= len(train_corpus) - 1:
                doc = Docs(" ".join(train_corpus[idx].words), cos_sim)
                documents.append(doc)
                count += 1
        return jsonpickle.encode(documents)
    except Exception as e:
        return jsonify([])


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002)
