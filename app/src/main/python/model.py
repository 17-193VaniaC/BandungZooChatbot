import tensorflow as tf
import tensorflow_hub as tfhub
import numpy as np
from os.path import dirname, join
#filename = join("src/main/use_lite.tar.gz", "use_lite.tar.gz")

model = tfhub.load("https://tfhub.dev/google/universal-sentence-encoder-lite/2")
#model ya modelnya
batch_size = 10 #banyakbatch
embeddings = []  #vector embedding pertanyaan
questions_embeddings = []

def initial(array_pertanyaan):
    pertanyaanList = np.array(array_pertanyaan)
    print(array_pertanyaan)
    for i in range(0, len(pertanyaanList), batch_size):
        embeddings.append(model(pertanyaanList[i:i+batch_size]))
    questions_embeddings = tf.concat(embeddings, axis=0)
    return questions_embeddings
#questions_embeddings itu vector embedding pertanyaan dalam bentuk tf
def getIndex(question, qe):
    embedding = model([question,])#dapet vector pertanyaan dari user
    scores = qe @ tf.transpose(embedding)
    if(np.amax(tf.squeeze(scores).numpy())<0.5):
        return 10000
    return np.argmax(tf.squeeze(scores).numpy())
    #np.argmax return index dari array yang punya nilai paling besar
    #
