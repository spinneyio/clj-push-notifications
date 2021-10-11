(ns clj-push-notifications.core
  (:import
   java.io.FileInputStream
   java.io.ByteArrayInputStream
   (com.google.firebase FirebaseOptions FirebaseOptions$Builder FirebaseApp)
   com.google.auth.oauth2.GoogleCredentials
   (com.google.firebase.messaging FirebaseMessaging Message MulticastMessage
                                  Notification AndroidConfig AndroidNotification
                                  ApnsConfig Aps)))

(defn- string-to-stream [string]
  (-> string
      (.getBytes)
      (ByteArrayInputStream.)))

(defn init-firebase 
  ([credentials] (init-firebase credentials nil))
  ([credentials db-url]
   (let [refreshToken (string-to-stream credentials)
         builder (FirebaseOptions$Builder.)
         builder-with-cred (.setCredentials builder (GoogleCredentials/fromStream refreshToken))
         builder-with-db (if db-url
                           (.setDatabaseUrl builder-with-cred db-url)
                           builder-with-cred)
         options (.build builder-with-db)]
     (FirebaseApp/initializeApp options))))

(defn delete-firebase []
  (.delete (FirebaseApp/getInstance)))

(defn- get-notification [builder title message badge custom-field custom-data type]
  (assert (string? custom-field))
  (assert (int? badge))
  (assert (string? type))
  (let [notification (Notification. title message)

        android-notification  (doto (AndroidNotification/builder)
                                (.setIcon "stock_ticker_update")
                                (.setColor  "#f45342")
                                (.setTitle title)
                                (.setClickAction type)
                                (.setNotificationCount badge))

        android-notification (.build android-notification)

        android-config  (doto (AndroidConfig/builder)
                          (.setTtl (* 3600 1000))
                          (.setNotification android-notification)
                          (.putData custom-field custom-data))

        android-config (.build android-config)

        aps (doto (Aps/builder)
              (.setBadge badge)
              (.putCustomData custom-field custom-data)
              (.setCategory type))

        aps (.build aps)

        apns-config (doto (ApnsConfig/builder)
                      (.setAps aps))

        apns-config (.build apns-config)

        message (doto builder
                  (.setNotification notification)
                  (.setAndroidConfig android-config)
                  (.setApnsConfig apns-config))]
    message))


(defn send-notification [{:keys [token title message badge custom-field custom-data type]}]
  (let [custom-field (or custom-field "")
        custom-data (or custom-data "")
        message (get-notification (Message/builder) title message badge custom-field custom-data type)
        message-with-token (.setToken message token)
        builded (.build message-with-token)]
    (future (.send (FirebaseMessaging/getInstance) builded))))

(defn send-multicast-notifications [{:keys [tokens title message badge custom-field custom-data type]}]
  (let [custom-field (or custom-field "")
        custom-data (or custom-data "")
        message (get-notification (MulticastMessage/builder) title message badge custom-field custom-data type)
        message-with-token (.addAllTokens message tokens)
        built (.build message-with-token)]
    (future (.sendMulticast (FirebaseMessaging/getInstance) built))))
