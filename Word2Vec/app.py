import json
import jsonpickle

from flask import Flask, request, abort, jsonify
import bs4 as bs
import urllib.request
import re
import nltk
import lxml
from gensim.models import Word2Vec

app = Flask(__name__)


class Words:
    def __init__(self, key, score):
        self.key = key
        self.score = score

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)


@app.route('/word2vec', methods=['GET'])
def main():  # put application's code here
    # CreateModel
    # FindSimilarWords

    if 'query' in request.args:
        query = request.args['query']
    else:
        return jsonify([])

    scrapped_data = urllib.request.urlopen('https://en.wikipedia.org/wiki/Artificial_intelligence')
    content = scrapped_data.read()

    # with open("/Users/kalyansabbella/Documents/Test/Doc/dblp.xml", "r") as file:
    #     # Read each line in the file, readlines() returns a list of lines
    #     content = file.readlines()
    #     # Combine the lines in the list into a string
    #     content = " ".join(content)

    parsed_article = bs.BeautifulSoup(content, 'lxml')

    paragraphs = parsed_article.find_all('p')

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
    try:
        v1 = word2vec.wv.most_similar(query)
    except Exception:
        return jsonify([])

    v2 = []
    for x in v1:
        word = Words(x[0], x[1])
        v2.append(word)
    # word2vec.wv.save_word2vec_format("/Users/kalyansabbella/Documents/Test/w2c/w2v_model.bin", binary=True)
    return jsonpickle.encode(v2)


if __name__ == '__main__':
    app.run()
