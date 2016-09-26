(ns kabosch.core
  (:require [clojure.string :as s]))

(defn comma-separate [str]
  (s/split str #","))
(defn replace-empty-with-null [str]
  (let [split-sequence (comma-separate str)]
  (map #(if (= "" %) "NULL" %) split-sequence)))

(defn build-column-map 
  "cn is the list of column headers.  r is the row of data, and the result is a map with column headers for keys and row data for values" 
  [cn r] 
  (apply assoc {} (interleave cn (replace-empty-with-null r))))

(defn csv-seq-to-map 
  "take a sequence of rows from a csv file where the first row is the headings and the remaining rows are the data.  Turn each row into a map where the keys come from the first row and the values are from that row"
  [s a k]

  (let [
        column-names (replace-empty-with-null (first s))]
    (swap! a assoc k {:headers column-names
                      :data (doall 
                             (map #(build-column-map column-names %) (rest s)))
                     })
  )
)

(defn increment-map-value 
  "building up counts for values. Map contains keys of strings along with a count.  When this function is called, the count associated with c should be incremented.  If c does not exist in the map, it should be added with count 1"
  [m c]
  (assoc m c (inc (get m c 0)))
)

(defn count-column-values [s c]
  (let [
        column-names (comma-separate (first s))]
    (reduce (fn [m i] 
              (let [f-of-i (build-column-map column-names i)
                    column-value (get f-of-i c)
                   ]
                (increment-map-value m column-value))
            ) {} (rest s))
))


(defn count-result-column-values [s columnToCount resultColumn]
  (let [
        column-names (comma-separate (first s))]
    (reduce (fn [m i] 
              (let [f-of-i (build-column-map column-names i)
                    column-value (get f-of-i columnToCount)
                    result-value (get f-of-i resultColumn)
                    new-result-val-map (increment-map-value (get m result-value {}) column-value)
                   ]
                (assoc m result-value new-result-val-map))
            ) {} (rest s))
))
(defn increment-result-map-value [m cv rv]
  ;; expecting map in the form {resultval {column-val count}}
  (assoc m rv (increment-map-value (get m rv {}) cv))
)
(defn count-column [count-map [cn cv] result-value]
  (if (not-any? #(= cn %) ["ID" "Response"])
    (let [column-map (get count-map cn)]
      (assoc count-map cn (increment-result-map-value column-map cv result-value))
  )
    count-map
))
(defn count-row [count-map row-string column-names result-column]
  (let [row (build-column-map column-names row-string)
        result-value (get row result-column)
       ]
  (reduce #(count-column %1 %2 result-value) count-map row)
))
(defn count-all-results-values [s resultColumn]
  (let [
        column-names (comma-separate (first s))
        column-name-map 
               (reduce #(if (not-any? (fn [c] (= c %2)) ["ID" resultColumn])
                 (assoc %1 %2 {})
                 %1)
                 {} column-names)
       ]
       ; process each row
       (reduce #(count-row %1 %2 column-names resultColumn) column-name-map (rest s))
))
