import pickle
import random
import re

import jsonpickle
import numpy
from flask import Flask, request, jsonify
import nltk
from nltk.corpus import words
import lxml
from gensim.models import Word2Vec, KeyedVectors, Doc2Vec
from nltk.corpus import words, stopwords
import gensim.utils
from gensim.parsing.preprocessing import remove_stopwords, preprocess_string
from gensim.test.utils import get_tmpfile
from collections import Counter
import math
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.linear_model import LogisticRegression
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score
from fast_autocomplete import AutoComplete

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
    fname = get_tmpfile("/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/Word2Vec/Cbow/word2vec_model")
    return Word2Vec.load(fname)


def savemodel(filename, model):
    fname = get_tmpfile(filename)
    model.save(fname)


def gettraindata():
    vldb1 = open('/Users/kalyansabbella/Documents/Test/NB Classfier/vldb_train.txt', 'r').readlines()
    icse1 = open('/Users/kalyansabbella/Documents/Test/NB Classfier/icse_train.txt', 'r').readlines()
    vldb2 = open('/Users/kalyansabbella/Documents/Test/NB Classfier/vldb_test.txt', 'r').readlines()
    icse2 = open('/Users/kalyansabbella/Documents/Test/NB Classfier/icse_test.txt', 'r').readlines()
    sigmod = open('/Users/kalyansabbella/Documents/Test/NB Classfier/sigmod.txt', 'r').readlines()

    vldb1 += vldb2
    icse1 += icse2

    if(len(vldb1) < len(sigmod)):
        icse1 = random.choices(icse1, k=len(sigmod))
        sigmod = random.choices(sigmod, k=len(sigmod))
    else:
        icse1 = random.choices(icse1, k=len(vldb1))
        vldb1 = random.choices(vldb1, k=len(vldb1))


    return vldb1, icse1, sigmod


def create_classifier_model():
    vldb, icse, sigmod = gettraindata()
    vectorizer = CountVectorizer()
    X = vectorizer.fit_transform(vldb + icse + sigmod).toarray()
    y = [1] * len(vldb) + [0] * len(icse) + [2] * len(sigmod)

    logreg = LogisticRegression(solver='saga')
    logreg.fit(numpy.array(X), numpy.array(y))

    pickle.dump(logreg, open('/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/NbClassifier/classifier.pkl', 'wb'))
    pickle.dump(vectorizer, open('/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/NbClassifier/vectorizer.pkl', 'wb'))


def fetch_classifier_model():
    model = pickle.load(open('/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/NbClassifier/classifier.pkl', 'rb'))
    vectorizer = pickle.load(open('/Users/kalyansabbella/Documents/UoW/Term 1/IR/Project/Resources/Models/NbClassifier/vectorizer.pkl', 'rb'))
    return model, vectorizer


def create_autocomplete_model():
    # with open('/Users/kalyansabbella/Documents/Test/words_dblp.txt', 'r') as f:
    with open('/Users/kalyansabbella/Documents/Test/words_alpha.txt', 'r') as f:
        words = f.readlines()
    words_dict = {word : {} for word in words}
    autocomplete = AutoComplete(words=words_dict)
    return autocomplete


d2v_glob = True
w2v_glob = True
nbclassifier_glob = True
autocomplete_glob = True

if d2v_glob:
    d2v_trainCorpus_glob = gettraincorpus(flag_new=False, flag_d2v=True)
    d2v_model_glob = fetch_doc2vec_model()

if w2v_glob:
    w2v_model_glob = fetch_word2vec_model()

if nbclassifier_glob:
    nbmodel_glob, nbvectorizer_glob = fetch_classifier_model()

if autocomplete_glob:
    autcomplete_model_glob = create_autocomplete_model()


class Words:
    def __init__(self, key, score):
        self.key = key
        self.score = score


class Docs:
    def __init__(self, key, score):
        self.key = key
        self.score = score


@app.route('/word2vec', methods=['GET'])
def word2vec():
    try:
        # create_word2vec_model()
        # word2vec = fetch_word2vec_model()
        word2vec = w2v_model_glob

        if 'query' in request.args:
            query = request.args['query']
        else:
            return jsonify([])

        queryList = query.lower().split()
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
        doc2vec = d2v_model_glob

        if 'query' in request.args:
            query = request.args['query']
        else:
            return jsonify([])

        # train_corpus = gettraincorpus(newFlag=False)
        train_corpus = d2v_trainCorpus_glob

        query_token = query.lower().split()
        inferred_vector = doc2vec.infer_vector(query_token)
        sims = doc2vec.dv.most_similar([inferred_vector], topn=20)
        documents = []
        count = 0
        for i in range(len(sims)):
            if count == 5:
                break
            idx, cos_sim = sims[i]
            if idx <= len(train_corpus) - 1:
                doc_title = " ".join(train_corpus[idx].words)
                if doc_title != query.lower():
                    doc = Docs(doc_title, cos_sim)
                    documents.append(doc)
                    count += 1
        return jsonpickle.encode(documents)
    except Exception as e:
        return jsonify([])


@app.route('/classify', methods=['POST', 'GET'])
def classify():
    if request.method == 'GET':
        query = ''
        if 'query' in request.args:
            query = request.args['query']
        else:
            return jsonify([])
        queryList = [query]
    if request.method == 'POST':
        queryList = request.get_json()['query']

    # create_classifier_model()
    # model, vectorizer = fetch_classifier_model()
    model = nbmodel_glob
    vectorizer = nbvectorizer_glob

    query_vector = vectorizer.transform(queryList).toarray()

    prediction = model.predict(query_vector)

    return jsonpickle.encode(int(prediction[0]))


@app.route('/autocomplete', methods=['GET'])
def autocomplete():
    if 'query' in request.args:
        query = request.args['query']
    else:
        return jsonify([])
    final_suggestions = []
    try:
        autocomplete = autcomplete_model_glob
        suggestions = autocomplete.search(word=query, max_cost=10, size=10)
        for suggest in suggestions[1:]:
            for s in suggest:
                if s != query+'\n' and s not in final_suggestions:
                    final_suggestions.append(s[:len(s)-1])
    except Exception as e:
        return jsonify([])
    return jsonpickle.encode(final_suggestions)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5002)
