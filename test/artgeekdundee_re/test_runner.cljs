(ns artgeekdundee-re.test-runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [artgeekdundee-re.events-test]))

(doo-tests 'artgeekdundee-re.events-test)
