(ns clj-atl-meetup-backend.routes.services
  (:require [ring.util.http-response :refer :all]
            [cheshire.core :as json]
            [clj-http.lite.client :as http]
            [clojure.string :as string]
            [compojure.api.sweet :refer :all]
            [schema.core :as s]))


(def my-distance
  {:destination_addresses
   ["101 W Chapel Hill St #300, Durham, NC 27701, USA"],
   :origin_addresses
   ["17 Executive Park Dr NE, Atlanta, GA 30329, USA"],
   :rows
   [{:elements
     [{:distance {:text "600 km", :value 600437},
       :duration {:text "5 hours 34 mins", :value 20055},
       :status "OK"}]}],
   :status "OK"}
  #_{:destination_addresses
   ["1535 Lake Paradise Rd, Villa Rica, GA 30180, USA"],
   :origin_addresses ["2210 Ashton Dr, Villa Rica, GA 30180, USA"],
   :rows
   [{:elements
     [{:distance {:text "20.6 km", :value 20604},
       :duration {:text "23 mins", :value 1364},
       :status "OK"}]}],
   :status "OK"})


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


(def distance-api
  "Google Maps API.
See: https://developers.google.com/maps/documentation/distance-matrix/intro
#DistanceMatrixRequests"
  ;; Move key to env.
  (str "https://maps.googleapis.com/maps/api/distancematrix/json"
       "?key=AIzaSyAu8SYl9_90jSuvy6L47M5xkWvmnMsoMJo"))


(defn get-travel-distance [starting-address ending-address]
  (let [start (string/replace starting-address #" " "+")
        end (string/replace ending-address #" " "+")]
    (with-log
      (http/get (str distance-api "&origins=" start "&destinations=" end)))))

(defn get-distance-response
  ([]
   ;;(get-distance-response "2210 ashton dr, villa rica, ga, 30180" "1535 lake paradise rd, villa rica, ga, 30180")
   (get-distance-response my-distance))
  ([pre-result]
   (let [main-result (-> pre-result
                         (get :rows)
                         first
                         (get :elements)
                         first)
         travel-time (-> main-result
                         (get :duration)
                         (get :text))
         status (get main-result :status)]
     travel-time))
  ([start end]
   (let [response (get-travel-distance start end)
         pre-result (-> response
                        :body
                        (json/parse-string true))
         main-result (-> pre-result
                         (get :rows)
                         first
                         (get :elements)
                         first)
         travel-time (-> main-result
                         (get :duration)
                         (get :text))
         status (get main-result :status)]
     travel-time)))


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
          (ring.util.http-response/header "Access-Control-Allow-Origin" "*"))


      ;;(get-distance-response)
      ))

  (comment
    (context "/api" []
      :tags ["thingie"]

      (GET "/plus" []
        :return       Long
        :query-params [x :- Long, {y :- Long 1}]
        :summary      "x+y with query-parameters. y defaults to 1."
        (ok (+ x y)))

      (POST "/minus" []
        :return      Long
        :body-params [x :- Long, y :- Long]
        :summary     "x-y with body-parameters."
        (ok (- x y)))

      (GET "/times/:x/:y" []
        :return      Long
        :path-params [x :- Long, y :- Long]
        :summary     "x*y with path-parameters"
        (ok (* x y)))

      (POST "/divide" []
        :return      Double
        :form-params [x :- Long, y :- Long]
        :summary     "x/y with form-parameters"
        (ok (/ x y)))

      (GET "/power" []
        :return      Long
        :header-params [x :- Long, y :- Long]
        :summary     "x^y with header-parameters"
        (ok (long (Math/pow x y)))))))
