(ns kabosch.core-test
  (:require [clojure.test :refer :all]
            [kabosch.core :refer :all]))

(deftest reader-testing
  (testing "Keys from the first line of a file"
    (let [s ["key1,key2,key3"
             "Avalue1,Avalue2,Avalue3"
             "Bvalue1,Bvalue2,Bvalue3"]
          oneatom (atom {})
          buildTheAtom (csv-seq-to-map s oneatom :testkey)
          ;helper (println @oneatom)
         ]
      (do (is (= ["key1" "key2" "key3"] (:headers (:testkey @oneatom))))
          (is (= '({"key1" "Avalue1" "key2" "Avalue2" "key3" "Avalue3"} 
                   {"key1" "Bvalue1" "key2" "Bvalue2" "key3" "Bvalue3"} )
              (:data (:testkey @oneatom))))
    ))
))

(deftest count-column-values-testing
  (testing "no rows"
    (let [columnToCount "response"
          s [(str "key1,key2," columnToCount)]
         ]
    (is (= {} (count-column-values s columnToCount))))
  )
  (testing "single row"
    (let [columnToCount "response"
          s [(str "key1,key2," columnToCount)
             "0,0,1"]
         ]
    (is (= {"1" 1} (count-column-values s columnToCount))))
  )
  (testing "two rows"
    (let [columnToCount "response"
          s [(str "key1,key2," columnToCount)
             "0,0,1"
             "0,0,1"]
         ]
    (is (= {"1" 2} (count-column-values s columnToCount))))
  )
)

(deftest result-column-count
  (testing "no rows"
    (let [resultColumn "Response"
          columnToCount "L0_S0_F0"
          s [(str "ID," columnToCount "," resultColumn)]
         ]
      (is (= {} (count-result-column-values s columnToCount resultColumn)))))
  (testing "one row"
    (let [resultColumn "Response"
          columnToCount "L0_S0_F0"
          s [(str "ID," columnToCount "," resultColumn)
             "4,0.35,1"]
         ]
      (is (= {"1" {"0.35" 1}} (count-result-column-values s columnToCount resultColumn)))))
)

(deftest result-all-column-counts
  (testing "no rows"
    (let [resultColumn "Response"
          s [(str "ID," "col1,col2,col3," resultColumn)] ]
      (is (= {"col1" {} "col2" {} "col3" {}} 
             (count-all-results-values s resultColumn)))
    ))
  (testing "one row"
    (let [resultColumn "Response"
          s [(str "ID," "col1,col2,col3," resultColumn)
             "4,1.2,30,-1.41,1"
            ]
         ]
      (is (= {"col1" {"1" {"1.2" 1}} 
               "col2" {"1" {"30" 1}}
               "col3" {"1" {"-1.41" 1}}}
             (count-all-results-values s resultColumn)))
    ))
  (testing "one row with empty value"
    (let [resultColumn "Response"
          s [(str "ID," "col1,col2,col3," resultColumn)
             "4,1.2,,-1.41,1"
            ]
         ]
      (is (= {"col1" {"1" {"1.2" 1}} 
               "col2" {"1" {"NULL" 1}}
               "col3" {"1" {"-1.41" 1}}}
             (count-all-results-values s resultColumn)))
    ))
  (testing "two rows same result"
    (let [resultColumn "Response"
          s [(str "ID," "col1,col2,col3," resultColumn)
             "4,1.2,30,-1.41,1"
             "6,1.4,30,-1.42,1"
            ]
         ]
      (is (= {"col1" {"1" {"1.2" 1 "1.4" 1}} 
               "col2" {"1" {"30" 2}}
               "col3" {"1" {"-1.41" 1 "-1.42" 1}}}
             (count-all-results-values s resultColumn)))
    ))
  (testing "two rows different result"
    (let [resultColumn "Response"
          s [(str "ID," "col1,col2,col3," resultColumn)
             "4,1.2,30,-1.41,1"
             "6,1.4,30,-1.42,0"
            ]
         ]
      (is (= {"col1" {"1" {"1.2" 1}
                      "0" {"1.4" 1}
                     } 
               "col2" {"1" {"30" 1}
                       "0" {"30" 1}
                     }
               "col3" {"1" {"-1.41" 1}
                       "0" {"-1.42" 1}}}
             (count-all-results-values s resultColumn)))
    ))
)
