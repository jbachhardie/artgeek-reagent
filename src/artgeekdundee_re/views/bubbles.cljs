(ns artgeekdundee-re.views.bubbles
  (:require [re-frame.core :as rf]))

(defn bubble
  "Single bubble for a gallery/exhibition"
  [exhibition color]
  [:div.Bubble
   [:a {:href     "#info"
        :on-click (fn [e] (rf/dispatch [:set-focus exhibition color])
                          (rf/dispatch [:scroll-to "info"])
                          (.preventDefault e))}
    (if (:upcoming? exhibition)
        [:div.BubbleComingSoon {:style {:color color}} "Coming Soon"])
    [:img {:style {:background-color color}
           :src (:thumbnail exhibition)}]]])

(defn container
  "Bubbles you can click on for exhibitions!"
  []
  [:section#menu.BubbleList
   [:h2 "This month's exhibits"]
   (map (fn [exhibition color] ^{:key exhibition} [bubble exhibition color])
        (concat @(rf/subscribe [:current-exhibitions])
                @(rf/subscribe [:upcoming-exhibitions]))
        @(rf/subscribe [:colors]))])
