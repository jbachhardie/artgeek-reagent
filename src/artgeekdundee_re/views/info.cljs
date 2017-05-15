(ns artgeekdundee-re.views.info
  (:require [reagent.core :as reagent]
            [clojure.string :as str]
            [re-frame.core :as rf]))

(defn exhibition-info
  "Image and blurb for the exhibition"
  [{:keys [image blurb title]}]
  [:div.Blurb-content
   [:img {:alt title
          :src image}]
   [:div.Blurb-text
    [:h2 title]
    [:div
     (for [line blurb]
       ^{:key line} [:div line])]]])

(defn map-circle
  "Link to the gallery location"
  [{:keys [location thumbnail]} color]
  [:a.MapCircle {:href location
                 :target "_blank"
                 :style {:color color
                         :background-color color}}
   [:div {:style {:background-image (str "url(" thumbnail ")")}}
    [:div.Icon
     [:i.fa.fa-map-marker]]]])

(defn gallery-info
  "Informational blurb about the gallery"
  [{:keys [name blurb]} color]
  [:div.GalleryInfo {:style {:background-color color}}
   [:h3 name]
   [:p blurb]])

(defn hours
  "Shows the hours the exhibition is on"
  [{:keys [begins ends]} times color]
  [:div.Hours
   [:div.Hours-Triangle {:style {:border-top (str "250px solid " color)}}]
   [:h3 (if (and begins ends) (str begins " - " ends) "Hours")]
   (for [section times :let [[day time] (str/split section #"---")]]
     ^{:key section} [:div
                      [:h4 day]
                      [:p time]])])

(defn return-to-top
  "Link to return to the top of the page" [color]
  [:a.Info-ReturnLink {:href     "#menu"
                       :on-click (fn [e]
                                   (rf/dispatch [:scroll-to "menu"])
                                   (.preventDefault e))}
   [:i.fa.fa-chevron-up.fa-4x {:style {:color color}}]])

(defn container
  "Container for all the exhibition info" []
  (let [exhibition @(rf/subscribe [:focus])
        gallery (:gallery exhibition)
        color @(rf/subscribe [:color])
        exhibition-times (or (:times exhibition)
                             (:times gallery))]
    [:section#info.Info
     [:div.Blurb {:style {:background-color color}}
      [exhibition-info exhibition]
      [map-circle gallery color]
      [gallery-info gallery color]
      [hours exhibition exhibition-times color]
      [return-to-top color]]]))