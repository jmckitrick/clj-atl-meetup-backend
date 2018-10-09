(ns clj-atl-meetup-backend.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-atl-meetup-backend started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-atl-meetup-backend has shut down successfully]=-"))
   :middleware identity})
