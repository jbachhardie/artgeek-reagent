(ns artgeekdundee-re.views.app
  (:require [re-frame.core :as rf]
            [artgeekdundee-re.views.bubbles :as bubbles]
            [artgeekdundee-re.views.info :as info]))

(defn spinner
  "Loading spinner" []
  [:i.fa.fa-spinner.fa-5x.fa-spin])

(defn container
  "Main app component" []
  [:div.App
   [:section.App-header
    [:h1 "The ArtGeek Guide " [:strong "To Dundee"]]]
   (if @(rf/subscribe [:loading?]) [spinner] [bubbles/container])
   (when @(rf/subscribe [:focus-set?]) [info/container])])
