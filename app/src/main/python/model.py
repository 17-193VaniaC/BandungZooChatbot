from firebase import firebase
import tensorflow as tf
import tensorflow_hub as tfhub
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity


firebase = firebase.FirebaseApplication("https://bandungzoochatbot-default-rtdb.firebaseio.com/", None)
brainfile_data = firebase.get('/Brainfile/', '')
pertanyaan = []
jawaban = []
for data in brainfile_data:
  qna = data
  if(type(qna) is dict):
    pertanyaan.append(qna.get('pertanyaan'))
    jawaban.append(qna.get('jawaban'))

model = tfhub.load("https://tfhub.dev/google/universal-sentence-encoder/4")

batch_size = 10
embeddings = []
qe = []
for i in range(0, len(pertanyaan), batch_size):
    embeddings.append(model(pertanyaan[i:i+batch_size]))
questions_embeddings = tf.concat(embeddings, axis=0)

def getAnswer(question: str) -> str:
    embedding = model([question,])
    cosine = cosine_similarity(embedding, questions_embeddings)
    return jawaban[np.argmax(tf.squeeze(cosine).numpy())]
