(ns artgeekdundee-re.effects
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]
            [promesa.core :as p]))

(s/def ::client object?)
(s/def ::method #{"getEntry" "getEntries"})
(s/def ::args (s/coll-of any?))
(s/def ::on-success keyword?)
(s/def ::on-failure keyword?)
(s/def ::contentful (s/keys :req-un [::client ::method ::args ::on-success ::on-failure]))

(rf/reg-fx
  :contentful
  (fn [{:keys [client method args on-success on-failure]}]
    (->> (apply (aget client method) args)
         (p/map #(js->clj %1 :keywordize-keys true))
         (p/map #(rf/dispatch [on-success %1]))
         (p/error #(rf/dispatch [on-failure %1])))))

(rf/reg-fx
  :scroll-to
  (fn [target]
    (js/smoothScroll.animateScroll target)))