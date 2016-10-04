(ns kabosch.runner
  (:require [kabosch.core :refer :all]
            [kabosch.value :refer :all]
            [clojure.java.io :as io]))

(def oneatom (atom {}))



(use 'clojure.java.io)

;; want to read a file in using some function f and store the results in an atom
;; that can be used later on. Probably need to provide the atom, and the key
;; that the results should be stored under
;;

(def oneatom (atom {}))


(def filename "../data/dev.csv")
(def count-filename "../data/dev_numeric_100.csv")
;(def count-filename "../data/train_numeric.csv")
(def value-count-filename "../data/numeric_value_counts.txt")


(defn read-file-data [f filename a k]
;; function to process each row
;; atom to store the result in
;; key to put in the atom
(with-open [rdr (reader filename)]
  (f (line-seq rdr) a k)
))
(defn read-file-data-2 [f filename k]
;; function to process each row
;; atom to store the result in
;; column heading to count
(with-open [rdr (reader filename)]
  ;(f (line-seq rdr) k)
  (f (take 100 (line-seq rdr)) k)
))
(defn read-file-data-3 [f filename k r]
;; function to process each row
;; atom to store the result in
;; column heading to count
;; column heading of the result column
(with-open [rdr (reader filename)]
  (f (line-seq rdr) k r)
))


(defn counts-for-key [k]
  (assoc {} k (read-file-data-3 count-result-column-values count-filename k "Response")))


(defn -main []
  (read-file-data csv-seq-to-map filename oneatom :theData)
  ;;(println "Response counts" (read-file-data-2 count-column-values count-filename "Response"))
  ;;(println "L0_S0_F0 counts" (read-file-data-2 count-column-values count-filename "L0_S0_F0"))
  ;;(println "L0_S0_F0 counts" (read-file-data-3 count-result-column-values count-filename "L0_S0_F0" "Response"))
  ;;(println "L0_S0_F4 counts" (read-file-data-3 count-result-column-values count-filename "L0_S0_F4" "Response"))
  ;;(println "There are " (count (:theData @oneatom)) " in the atom")
  ;;(doall (map println (:theData @oneatom)))

  ;(def column-keys ["L0_S0_F0"  "L0_S0_F2"  "L0_S0_F4"  "L0_S0_F6"  "L0_S0_F8"
  ;                  "L0_S0_F10" "L0_S0_F12" "L0_S0_F14" "L0_S0_F16" "L0_S0_F18"
  ;                  "L0_S0_F20"
  ;                 ])
  ;(def column-keys ["L0_S0_F22" "L0_S1_F24" "L0_S1_F28" "L0_S2_F32" "L0_S2_F36"
  ;                  "L0_S2_F40" "L0_S2_F44" "L0_S2_F48" "L0_S2_F52" "L0_S2_F56"
  ;                  "L0_S2_F60" "L0_S2_F64" "L0_S3_F68" "L0_S3_F72" "L0_S3_F76"
  ;                  "L0_S3_F80" "L0_S3_F84" "L0_S3_F88" "L0_S3_F92" "L0_S3_F96"
  ;                  "L0_S3_F100"
  ;                 ])
  (def column-keys [
       "L0_S4_F104" "L0_S4_F109" "L0_S5_F114" "L0_S5_F116" "L0_S6_F118"
       "L0_S6_F122" "L0_S6_F132" "L0_S7_F136" "L0_S7_F138" "L0_S7_F142" 
       "L0_S8_F144" "L0_S8_F146" "L0_S8_F149" "L0_S9_F155" "L0_S9_F160" 
       "L0_S9_F165" "L0_S9_F170" "L0_S9_F175" "L0_S9_F180" "L0_S9_F185" 
       "L0_S9_F190" "L0_S9_F195" "L0_S9_F200"
                   ])
  ;(doall (map #(println (counts-for-key %)) column-keys))
  ;(println (read-file-data-2 count-all-results-values count-filename "Response"))
  (def counts (read-string (slurp value-count-filename)))
  ;(println (first counts))

  (println (sort-by (fn [x] (get x 2)) (filter #(not (= 'Id (get % 0))) (filter #(> (get % 2) 0.1)(reduce into [] (map calculate-values-with-key counts))))))
)
