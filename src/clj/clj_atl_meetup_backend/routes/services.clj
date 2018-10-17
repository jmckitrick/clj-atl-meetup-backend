(ns clj-atl-meetup-backend.routes.services
  (:require [ring.util.http-response :refer :all]
            [cheshire.core :as json]
            [clj-http.lite.client :as http]
            [clojure.string :as string]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [clj-atl-meetup-backend.config :refer [env]]))


(def show-db-debug-info (atom true))


(defmacro with-log
  "Catch and log exceptions."
  [& body]
  `(try
     ~@body
     (catch Exception e#
       (when @show-db-debug-info
         (println (str "---> exception " e#))
         (println (str "---> stack trace " (.printStackTrace e#)))
         (println (str "---> next exception " (.getNextException e#))))
       (throw e#))))







(defn distance-api []
  (str "https://maps.googleapis.com/maps/api/distancematrix/json"
       "?key=" (-> env :api-key)))


(defn get-travel-distance [starting-address ending-address]
  (println starting-address ending-address)
  (let [start (string/replace starting-address #" " "+")
        end (string/replace ending-address #" " "+")
        url (str (distance-api) "&origins=" start "&destinations=" end)]
    (with-log
      ;; json response
      (println url)
      #_(http/get url)
      (-> (http/get url) :body (json/parse-string true)))))


















(def my-distance
  {:destination_addresses ["New York, NY, USA"],
 :origin_addresses ["Atlanta, GA, USA"],
 :rows
 [{:elements
   [{:distance {:text "1,390 km", :value 1390310},
     :duration {:text "13 hours 10 mins", :value 47416},
     :status "OK"}]}],
   :status "OK"}
  #_
  {:destination_addresses
   ["101 W Chapel Hill St #300, Durham, NC 27701"],
   :origin_addresses
   ["17 Executive Park Dr NE, Atlanta, GA 30329"],
   :rows
   [{:elements
     [{:distance {:text "600 km", :value 600437},
       :duration {:text "5 hours 34 mins", :value 20055},
       :status "OK"}]}],
   :status "OK"})














(defn get-distance-response-exp [result]
  (-> result :rows first :elements first :duration :text))

(defn get-distance-response [start end]
  (let [result (get-travel-distance start end)]
    (-> result :rows first :elements first :duration :text)))



























(defapi service-routes
  {:swagger {:ui "/swagger-ui"
             :spec "/swagger.json"
             :data {:info {:version "1.0.0"
                           :title "Sample API"
                           :description "Sample Services"}}}}

  (context "/demo" []
    (GET "/distance" []
      :return String
      :query-params [start :- String, end :- String]
      :summary "Start and end addresses with comma-separated fields"
      (-> (get-distance-response start end)
          ring.util.response/response
          (ring.util.http-response/header "Access-Control-Allow-Origin" "*")))))
