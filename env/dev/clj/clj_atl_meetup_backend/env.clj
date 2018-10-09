(ns clj-atl-meetup-backend.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [clj-atl-meetup-backend.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clj-atl-meetup-backend started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-atl-meetup-backend has shut down successfully]=-"))
   :middleware wrap-dev})
