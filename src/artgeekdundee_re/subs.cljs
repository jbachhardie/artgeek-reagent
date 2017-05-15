(ns artgeekdundee-re.subs
  (:require [re-frame.core :as rf]
            [cljs-time.core :as time]
            [cljs-time.format :as format]
            [artgeekdundee-re.utils.text :as text]))

(defn format-date [date]
  (->> (format/parse (format/formatters :date) date)
       (format/unparse {:format-str "MMM dd"})))

(defn resolve-media [media] (-> media :fields :file :url))

(defn before-today?
  [time-string]
  (time/before? (time/today-at 0 0)
                (format/parse time-string)))

(defn get-exhibitions
  [db]
  (->> (-> db :exhibitions :items)
       (map :fields)
       (map #(update %1 :thumbnail resolve-media))
       (map #(assoc %1 :gallery (-> %1 :gallery :fields)))))

(rf/reg-sub
  :loading?
  (fn [db _]
    (or (empty? (:config db))
        (empty? (:exhibitions db)))))

(rf/reg-sub
  :current-exhibitions
  (fn [db _]
    (->> (get-exhibitions db)
         (filter #(before-today? (:ends %1)))
         (filter #(not (before-today? (:begins %1)))))))

(rf/reg-sub
  :upcoming-exhibitions
  (fn [db _]
    (->> (get-exhibitions db)
         (filter #(before-today? (:begins %1)))
         (map #(assoc %1 :upcoming? true)))))

(rf/reg-sub
  :colors
  (fn [db _]
    (-> db :config :fields :colors)))

(rf/reg-sub
  :focus
  (fn [db _]
    (when-some [focus (:focus db)]
               (-> focus
                   (update :begins format-date)
                   (update :ends format-date)
                   (update :blurb text/process-blurb)
                   (update :image resolve-media)
                   (update-in [:gallery :thumbnail] resolve-media)))))

(rf/reg-sub
  :focus-set?
  (fn [db _]
    (not (empty? (:focus db)))))

(rf/reg-sub
  :color
  (fn [db _]
    (:focus-color db)))