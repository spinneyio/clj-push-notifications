# Clojure FCM client
Clojure wrap for Firebase Cloud Messaging that allows You to send push notifications easily.

### Installation
Leiningen coordinates:
```clj
[clj-push-notifications "1.0.0"]
```
### Example of usage:
First get Your credentials (google-services.json file) from Firebase console, and add them to Your project path (or some config file):
```clj
(ns example.firebase-notifications
    (:require [mount.core :refer [defstate]]
              [fcm-client.core :as fcm]))

(defstate firebase-notifications
    ; You can check Your db-url in: Firebase Console -> Settings -> Service Accounts.
    :start  (init-firebase (slurp config-file-path) db-url)
    :stop  (.delete (FirebaseApp/getInstance)))

(def notification-params (-> {}
                            (assoc :token "some-firebase-token")
                            (assoc :title "Hi,")
                            (assoc :message "Hello world!")
                            (assoc :badge 1)
                            (assoc :custom-field "my-field")
                            (assoc :custom-data "Some data")))

(def multicast-notification-params (-> notification-params
                                        (dissoc :token)
                                        (assoc :tokens ["token-1" "token-2" ...])))

(fcm/send-notification notification-params)
(fcm/send-multicast-notifications multicast-notification-params)
```


License
----
Distributed under the EPL v2.0.
Copyright &copy; Spinney.
