(ns kabosch.value-test
  (:require [clojure.test :refer :all]
            [kabosch.value :refer :all]))

(def example1 
   (read-string "[L1_S24_F1637 {1 {0.299 1, NULL 2}, 0 {0.354 1, NULL 95}}]"))

(deftest attribute-value-testing
  (testing "Values with scores of 1"
    (is (= '(0.299 NULL) (one-keys example1))))
  (testing "Values with scores of 0"
    (is (= '(0.354 NULL) (zero-keys example1))))
  (testing "Values for all Responses"
    (is (= #{0.299 0.354 'NULL} (all-keys example1))))
)

(deftest calculate-attribute-value
  (testing "no 0 responses"
    (let [one-map {1 {3.4 1}} 
          ones (get one-map 1)
          zero-map {0 {3.0 1}}
          zeros (get zero-map 0)
         ]
      (is (= [3.4 1] (calculate-value 3.4 ones zeros)))
    ))
  (testing "no 1 responses"
    (let [one-map {1 {3.4 1}} 
          ones (get one-map 1)
          zero-map {0 {3.0 1}}
          zeros (get zero-map 0)
         ]
      (is (= [3.0 0] (calculate-value 3.0 ones zeros)))
    ))
  (testing "some of each response"
    (let [one-map {1 {3.4 1}} 
          ones (get one-map 1)
          zero-map {0 {3.4 3}}
          zeros (get zero-map 0)
         ]
      (is (= [3.4 1/4] (calculate-value 3.4 ones zeros)))
    ))
)
(def example2 ["L1_S24_F1637" {1 {0.299 1, 'NULL 2}, 0 {0.354 1, 'NULL 95}}])

(deftest calculate-all-values
  (testing "iiii"
    (is (= (set '([NULL 2/97] [0.299 1] [0.354 0]))
           (set (calculate-values example2))))
  )
)


(deftest  display-value-range
  (testing "value list"
    (is (= [['NULL [95 2]] [0.299 [0 1]] [0.354 [1 0]]]
           (ordered-value-list (first (rest example2)))))
))
