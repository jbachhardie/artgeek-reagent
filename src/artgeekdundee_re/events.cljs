(ns artgeekdundee-re.events
  (:require [re-frame.core :as rf]
            [cljs.spec.alpha :as s]
            [cljs.spec.gen.alpha :as gen]
            [cljsjs.smooth-scroll]
            [cljs-time.core :as time]
            [cljs-time.coerce :as time-coerce]
            [artgeekdundee-re.effects]))

(s/def ::title string?)
(s/def ::name string?)
(s/def ::blurb string?)
(s/def ::description string?)
(s/def ::date (s/with-gen time/date? #(gen/fmap time-coerce/from-long (s/gen nat-int?))))
(s/def ::begins ::date)
(s/def ::ends ::date)
(s/def ::url string?)
(s/def ::location ::url)
(s/def ::file (s/keys :req-un [::url]))
(s/def ::image (s/keys :req-un [::file]
                       :opt-un [::title ::description]))
(s/def ::thumbnail ::image)
(s/def ::opening-time (s/and string?))
(s/def ::times (s/coll-of ::opening-time))
(s/def ::gallery (s/keys :req-un [::name ::blurb ::location ::thumbnail]
                         :opt-un [::times]))
(s/def ::exhibition (s/keys :req-un [::title ::blurb ::image ::thumbnail ::begins ::ends]
                            :opt-un [::gallery ::times]))
(s/def ::color string?)
(s/def ::client (s/with-gen object? #(s/gen #{#js{}})))
(s/def ::colors (s/coll-of ::color))
(s/def ::config (s/keys :req-un [::colors]))
(s/def ::exhibitions (s/coll-of ::exhibition))
(s/def ::focus ::exhibition)
(s/def ::focus-color ::color)
(s/def ::db (s/keys :req-un [::client ::config ::exhibitions]
                    :opt-un [::focus-color ::focus]))

(s/def ::contentful :artgeekdundee-re.effects/contentful)

(defn check-and-throw
  "throw an exception if db doesn't match the spec"
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor (rf/after (partial check-and-throw ::db)))

(def default-interceptors [check-spec-interceptor
                           (when ^boolean js/goog.DEBUG rf/debug)
                           rf/trim-v])

(defn init
  "Initial state, includes contentful client"
  [_ _]
  {:client (.createClient js/contentful
                          #js{:accessToken "a9eca5be56c5c684c02249acc9e65ccafe8caebd8135d3612a11ddb24496fbcd"
                              :space "43cqz5fm9g1u"})
   :config {:colors []}
   :exhibitions []
   :focus-color "#FFFFFF"})

(s/fdef init
        :args (s/cat :db nil? :args nil?)
        :ret ::db
        :fn (s/and #(object? (-> % :ret :client))
                   #(empty? (-> % :ret :exhibitions))
                   #(empty? (-> % :ret :config :colors))
                   #(nil? (-> % :ret :focus))
                   #(= (-> :ret :focus-color) "#FFFFFF")))

(rf/reg-event-db :init default-interceptors init)

(defn fetch-config
  "Fetches config item from contentful"
  [{:keys [db]} _]
  {:contentful {:client (:client db)
                :method "getEntry"
                :args ["5zSFnoooRqmIWqOUyEsWgo"]
                :on-success :load-config
                :on-failure :fetch-config}})

(s/fdef fetch-config
        :args (s/cat :cofx (s/keys :req-un [::db]) :args nil?)
        :ret (s/keys :req-un [::contentful])
        :fn #(object? (-> % :ret :contentful :client)))

(rf/reg-event-fx :fetch-config fetch-config)

(rf/reg-event-db
  :load-config
  (fn [db [_ config]]
    (assoc db :config config)))

(rf/reg-event-fx
  :fetch-exhibitions
  (fn [{:keys [db]} _]
    {:contentful {:client (:client db)
                  :method "getEntries"
                  :args [#js{:content_type "exhibition"}]
                  :on-success :load-exhibitions
                  :on-failure :fetch-exhibitions}}))

(rf/reg-event-db
  :load-exhibitions
  (fn [db [_ exhibitions]]
    (assoc db :exhibitions exhibitions)))

(rf/reg-event-db
  :set-focus
  (fn [db [_ exhibition color]]
    (-> db
        (assoc :focus exhibition)
        (assoc :focus-color color))))

(rf/reg-event-fx
  :scroll-to
  (fn [_ [_ id]]
    (if-let [target (.getElementById js/document id)]
            {:scroll-to target}
            {:dispatch-later [{:ms 300 :dispatch [:scroll-to id]}]})))

