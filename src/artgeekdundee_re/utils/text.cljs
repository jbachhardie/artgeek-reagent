(ns artgeekdundee-re.utils.text)

(defn pop-line
  "Takes a line of length from the front of a text, splitting in valid spots only"
  [text requested-length]
  (loop [length requested-length]
    (let [number-of-hyphens (count (re-seq #"\u00AD" text))
          adjusted-length (+ length number-of-hyphens)]
      (if (> length 0)
        (case (.charAt text adjusted-length)
          " " [(.slice text 0 adjusted-length) (.slice text (+ adjusted-length 1))]
          "\u00AD" [(str (.slice text 0 adjusted-length) "-") (str "-" (.slice text (+ adjusted-length 1)))]
          "" [text ""]
          (recur (- length 1)))
        [text ""]))))

(defn process-blurb
  "Splits a blurb into pieces of decreasing length" [raw]
  (loop [text raw
         length 76
         acc []]
    (let [number-of-hyphens (count (re-seq #"\u00AD" text))
          [line, rest] (pop-line text length)
          remove-hyphens (fn [line] (clojure.string/replace line #"\u00AD" ""))
          decrement-length (fn [length] (condp > length
                                          68 (- length 6)
                                          76 (- length 2)
                                          (- length 1)))]
      (if (and (> (- (count text) number-of-hyphens) length) (> length 0))
        (recur rest (decrement-length length) (conj acc (remove-hyphens line)))
        (conj acc (remove-hyphens line) (remove-hyphens rest))))))