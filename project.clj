(defproject artgeekdundee-re "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]
                 [org.clojure/clojurescript "1.9.542"]
                 [funcool/promesa "1.8.1"]
                 [reagent "0.6.1"]
                 [re-frame "0.9.2"]
                 [cljsjs/smooth-scroll "10.2.1-0"]
                 [com.andrewmcveigh/cljs-time "0.5.0-alpha2"]
                 [figwheel-sidecar "0.5.10-SNAPSHOT"]]

  :plugins [[lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]
            [lein-ancient "0.6.10"]
            [lein-doo "0.1.7"]]

  :source-paths ["src" "script"]

  :hooks [leiningen.cljsbuild]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :doo {:build "test"
        :alias {:default [:phantom]}}

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]
                :figwheel {:on-jsload "artgeekdundee-re.core/on-js-reload"
                           :open-urls ["http://localhost:3449/index.html"]}

                :compiler {:main artgeekdundee-re.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/artgeekdundee_re.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}

               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/artgeekdundee_re.js"
                           :main artgeekdundee-re.core
                           :optimizations :advanced
                           :externs ["externs/contentful.js"]
                           :pretty-print false}}

               {:id "test"
                :source-paths ["src" "test"]
                :compiler {:output-to "resources/private/js/unit-test.js"
                           :main artgeekdundee-re.test_runner
                           :optimizations :none
                           :pretty-print true}}]}


  :figwheel {:http-server-root "public" ;; default and assumes "resources"
             :server-port 3449
             :server-ip "127.0.0.1"

             :css-dirs ["resources/public/css"]} ;; watch and update CSS

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                                  [figwheel-sidecar "0.5.10-SNAPSHOT"]
                                  [org.clojure/test.check "0.9.0"]
                                  [com.cemerick/piggieback "0.2.1"]]
                   ;; need to add dev source path here to get user.clj loaded
                   :source-paths ["src" "dev"]
                   ;; for CIDER
                   ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}})


