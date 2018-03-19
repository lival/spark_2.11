/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.mllib.clustering

import java.util.{ArrayList => JArrayList}

import breeze.linalg.{argmax, argtopk, max, DenseMatrix => BDM}

import org.apache.spark.SparkFunSuite
import org.apache.spark.graphx.Edge
import org.apache.spark.mllib.linalg.{DenseMatrix, Matrix, Vector, Vectors}
import org.apache.spark.mllib.util.MLlibTestSparkContext
import org.apache.spark.mllib.util.TestingUtils._
import org.apache.spark.util.Utils

class LDASuite extends SparkFunSuite with MLlibTestSparkContext {

  import LDASuite._


  test("OnlineLDAOptimizer one iteration") {
    // run OnlineLDAOptimizer for 1 iteration to verify it's consistency with Blei-lab,
    // [[https://github.com/Blei-Lab/onlineldavb]]
    val k = 2
    val vocabSize = 6

    def docs: Array[(Long, Vector)] = Array(
      Vectors.sparse(vocabSize, Array(0, 1, 2), Array(1, 1, 1)), // apple, orange, banana
      Vectors.sparse(vocabSize, Array(3, 4, 5), Array(1, 1, 1)) // tiger, cat, dog
    ).zipWithIndex.map { case (wordCounts, docId) => (docId.toLong, wordCounts) }
    val corpus = sc.parallelize(docs, 2)

    // Set GammaShape large to avoid the stochastic impact.
    val op = new OnlineLDAOptimizer().setTau0(1024).setKappa(0.51).setGammaShape(1e40)
      .setMiniBatchFraction(1)
    val lda = new LDA().setK(k).setMaxIterations(1).setOptimizer(op).setSeed(12345)

    val state = op.initialize(corpus, lda)
    // override lambda to simulate an intermediate state
    //    [[ 1.1  1.2  1.3  0.9  0.8  0.7]
    //     [ 0.9  0.8  0.7  1.1  1.2  1.3]]
    op.setLambda(new BDM[Double](k, vocabSize,
      Array(1.1, 0.9, 1.2, 0.8, 1.3, 0.7, 0.9, 1.1, 0.8, 1.2, 0.7, 1.3)))

    // run for one iteration
    state.submitMiniBatch(corpus)

    // verify the result, Note this generate the identical result as
    // [[https://github.com/Blei-Lab/onlineldavb]]
    val topic1: Vector = Vectors.fromBreeze(op.getLambda(0, ::).t)
    val topic2: Vector = Vectors.fromBreeze(op.getLambda(1, ::).t)
    val expectedTopic1 = Vectors.dense(1.1101, 1.2076, 1.3050, 0.8899, 0.7924, 0.6950)
    val expectedTopic2 = Vectors.dense(0.8899, 0.7924, 0.6950, 1.1101, 1.2076, 1.3050)
    assert(topic1 ~== expectedTopic1 absTol 0.01)
    assert(topic2 ~== expectedTopic2 absTol 0.01)
  }
}

private[clustering] object LDASuite {

  def tinyK: Int = 3
  def tinyVocabSize: Int = 5
  def tinyTopicsAsArray: Array[Array[Double]] = Array(
    Array[Double](0.1, 0.2, 0.3, 0.4, 0.0), // topic 0
    Array[Double](0.5, 0.05, 0.05, 0.1, 0.3), // topic 1
    Array[Double](0.2, 0.2, 0.05, 0.05, 0.5) // topic 2
  )
  def tinyTopics: Matrix = new DenseMatrix(numRows = tinyVocabSize, numCols = tinyK,
    values = tinyTopicsAsArray.fold(Array.empty[Double])(_ ++ _))
  def tinyTopicDescription: Array[(Array[Int], Array[Double])] = tinyTopicsAsArray.map { topic =>
    val (termWeights, terms) = topic.zipWithIndex.sortBy(-_._1).unzip
    (terms.toArray, termWeights.toArray)
  }

  def tinyCorpus: Array[(Long, Vector)] = Array(
    Vectors.dense(0, 0, 0, 0, 0), // empty doc
    Vectors.dense(1, 3, 0, 2, 8),
    Vectors.dense(0, 2, 1, 0, 4),
    Vectors.dense(2, 3, 12, 3, 1),
    Vectors.dense(0, 0, 0, 0, 0), // empty doc
    Vectors.dense(0, 3, 1, 9, 8),
    Vectors.dense(1, 1, 4, 2, 6)
  ).zipWithIndex.map { case (wordCounts, docId) => (docId.toLong, wordCounts) }
  assert(tinyCorpus.forall(_._2.size == tinyVocabSize)) // sanity check for test data

  def getNonEmptyDoc(corpus: Array[(Long, Vector)]): Array[(Long, Vector)] = corpus.filter {
    case (_, wc: Vector) => Vectors.norm(wc, p = 1.0) != 0.0
  }

  def toyData: Array[(Long, Vector)] = Array(
    Vectors.sparse(6, Array(0, 1), Array(1, 1)),
    Vectors.sparse(6, Array(1, 2), Array(1, 1)),
    Vectors.sparse(6, Array(0, 2), Array(1, 1)),
    Vectors.sparse(6, Array(3, 4), Array(1, 1)),
    Vectors.sparse(6, Array(3, 5), Array(1, 1)),
    Vectors.sparse(6, Array(4, 5), Array(1, 1))
  ).zipWithIndex.map { case (wordCounts, docId) => (docId.toLong, wordCounts) }

  /** Used in the Java Test Suite */
  def javaToyData: JArrayList[(java.lang.Long, Vector)] = {
    val javaData = new JArrayList[(java.lang.Long, Vector)]
    var i = 0
    while (i < toyData.length) {
      javaData.add((toyData(i)._1, toyData(i)._2))
      i += 1
    }
    javaData
  }

  def toyModel: LocalLDAModel = {
    val k = 2
    val vocabSize = 6
    val alpha = 0.01
    val eta = 0.01
    val gammaShape = 100
    val topics = new DenseMatrix(numRows = vocabSize, numCols = k, values = Array(
      1.86738052, 1.94056535, 1.89981687, 0.0833265, 0.07405918, 0.07940597,
      0.15081551, 0.08637973, 0.12428538, 1.9474897, 1.94615165, 1.95204124))
    val ldaModel: LocalLDAModel = new LocalLDAModel(
      topics, Vectors.dense(Array.fill(k)(alpha)), eta, gammaShape)
    ldaModel
  }
}
