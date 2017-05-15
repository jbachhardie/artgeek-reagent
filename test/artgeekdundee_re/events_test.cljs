(ns artgeekdundee-re.events-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [cljs.spec.test.alpha :as stest]
            [artgeekdundee-re.events :as sut]
            [cljs.pprint :as pprint]))

;; Utility functions to intergrate clojure.spec.test/check with clojure.test
(defn summarize-results' [spec-check]
  (map (comp #(pprint/write % :stream nil) stest/abbrev-result) spec-check))

(defn check' [spec-check]
  (is (nil? (-> spec-check first :failure)) (summarize-results' spec-check)))

(deftest specs
  (check' (stest/check `sut/fetch-config)))