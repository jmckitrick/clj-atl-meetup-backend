(ns user
  (:require [clj-atl-meetup-backend.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [clj-atl-meetup-backend.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'clj-atl-meetup-backend.core/repl-server))

(defn stop []
  (mount/stop-except #'clj-atl-meetup-backend.core/repl-server))

(defn restart []
  (stop)
  (start))


