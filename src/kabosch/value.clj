(ns kabosch.value
  (:require [clojure.string :as s]))


;; calculating the usefulness of attribute values
;; by getting the percentage of the occurances that separate the results

;; data will be in a map created elsewhere
;; the top level key is the attribute
;; the value associated with that key is another map keyed by the response
;; and inside that is a map of value for the top level key and a count 
;; of how many times that value occured.

;; example (first key value pair in a test file)
;;  [L1_S24_F1637 {1 {0.299 1, NULL 2}, 0 {0.354 1, NULL 95}}]

(defn one-keys [[k v]]
  (keys (get v 1)))

(defn zero-keys [[k v]]
  (keys (get v 0)))

(defn all-keys [[k v]]
  (-> #{} (into (one-keys [k v])) 
          (into (zero-keys [k v])
)))

(defn calculate-value
  "calculate the ration of 1 responses to the total number of occurrences"
  [v ones zeros]
  (let [one-value-count (get ones v 0)
        zero-value-count (get zeros v 0)
        sum (+ one-value-count zero-value-count)
       ]
    [v (/ one-value-count sum)]))
(defn calculate-value-with-key
  "calculate the ration of 1 responses to the total number of occurrences"
  [k v ones zeros]
  (let [one-value-count (get ones v 0)
        zero-value-count (get zeros v 0)
        sum (+ one-value-count zero-value-count)
       ]
    [k v (/ one-value-count sum)]))

(defn calculate-values [[k m]]
  (let [one-map (get m 1)
        zero-map (get m 0)
        the-keys (all-keys [k m])]
   (map #(calculate-value % one-map zero-map) the-keys)))

(defn calculate-values-with-key [[k m]]
  (let [one-map (get m 1)
        zero-map (get m 0)
        the-keys (all-keys [k m])]
   (map #(calculate-value-with-key k % one-map zero-map) the-keys)))


(defn ordered-value-list [m]
  (let [zeros (reduce (fn [m [k v]] (assoc m k [v 0])) {} (get m 0))
        ones  (reduce (fn [m [k v]] (assoc m k [0 v])) {} (get m 1))
        map-of-totals (reduce (fn [m [k v]]
              (let [zero-map (get m k [0 0])]
                (assoc m k (vec (map + zero-map v))))) zeros ones)
       ]
    ;(vec (sort-by #(.toString (first %)) (reduce conj [] map-of-totals)))
    (vec (sort-by (fn [[x _ ]] (if (= 'NULL x) nil x)) (reduce conj [] map-of-totals)))
))
