(ns adventure.core
  (:gen-class))

(def init-map
  {
  :pen {:desc ""
           :title "in the Pen. There is a chicken that lays eggs in here"
           :dir {:south :crack-egg-room}
           :contents #{:raw-egg :chicken}
           :requires #{}
           }

  :crack-egg-room {:desc "You can crack somg eggs here."
            :title "in the grue pen"
            :dir {:north :pen}
            :contents #{}
            :requires #{:bowl :raw-egg}
            }
  }
)

(def init-items
 {
   :raw-egg {:desc "This is a raw egg.  You probably want to cook it before eating it."
            :name "a raw egg"
            :actions #{:take :crack}
             }

  :chicken {:desc "This is a chicken. You can pet it for some eggs."
            :name "a chicken" 
            :actions #{:pet}
            }
            
            
  })

(def init-adventurer
  {:location :pen
   :inventory #{}
   :hp 10
   :lives 3
   :tick 0
   :seen #{}})


; movement code


(defn go [state dir]
  (let [location (get-in state [:adventurer :location])
        dest ((get-in [:map location :dir] state) dir)]
    (if (nil? dest)
      (do (println "You can't go that way.")
          )
      (assoc-in state [:adventurer :location] dest))))

(defn status [state]
  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (str "You are " (-> the-map location :title) ". "))
    (when-not ((get-in state [:adventurer :seen]) location)
      (print (-> the-map location :desc)))
    (update-in state [:adventurer :seen] #(conj % location))))

(defn -main
  "Initialize the adventure"
  [& args]
  (loop [local-state {:map init-map :adventurer init-adventurer :items init-items}]
  (do 
  
    (status local-state)
  
  )

  )
)
