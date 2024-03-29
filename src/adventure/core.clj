(ns adventure.core
  (:gen-class))
(require '[clojure.string :as str])
(require 'clojure.string)
(use '[clojure.string :only (split triml capitalize)])

; (declare help)
(declare initial-env)

(def init-map
	{
	:pen {:desc "The ground is muddy and covered with Hay and Straw. There is a chicken that looks at you inquisitevely and with affection(?). It sits above a pale yellow egg. It looks strangely familiar. It soothes you."
			:title "in the Pen"
			:dir {:south :crack-egg-room
				  :north :garden
				  :east :kitchen
				  :west :preperation-room}
			:contents #{:raw-egg}
			:suspicion -2
			}

	:crack-egg-room {:desc "The room seems designed for an oddly specific task. You can crack some eggs here. You become uneasy."
			:title "in the crack egg room"
			:dir {:north :pen
				  :south :cutlery-room
				  :east :beat-egg-room}
			:contents #{}
			:suspicion 2
			}

	:garden {:desc "Fully grown vegetables sprawl around you in every direction as far as you can see. Neatly arranged, you see Cilantro on your left, Spring Onions in front of you, and Tomatoes to your right. The green makes you feel calm."
			:title "in the garden"
			:dir {:south :pen}
			:contents #{:cilantro :onion :tomato}
			:suspicion -1
	}

	:dining-room {:desc "You see a giant table strech the legnth of the room. A single throne-like chair sits at the head. In the center is a pristine glass sculpture of an egg.You look up and see a large dome extend from the middle of the cieling. Grand paintings depicting people from different eras interacting with chickens spread across the whole cieling. From the center of the dome descends an ornate chandelier studded with magnificent stones, each one precicesly cut. The grandiose makes you uncomfortable"
			:title "in the dining room"
			:dir {:west :kitchen}
			:contents #{}
			:suspicion +2
	}

	:kitchen {:desc "You are glad to see a room that looks familiar. However, it seems barren, save a few objects.There is a single flame stove, with a non-stick pan (that seems stuck to the stove) resting on top. In the pan is just a little bit of oil. Next to it is a kitchen timer, going up to 3 minutes."
			:title "in the kitchen"
			:dir {:west :pen
				  :east :dining-room}
			:contents #{}
			:suspicion 0
	}

	:cutlery-room {:desc "You open the door to a small room that reaches just above your head. It looks similar to a pantry.It is lined with shelves, each seperated from the next by about a foot, 5 in total. Each row has either bowls or forks, in an alternating fashion. You are more amused by the fact that everytime you open the door, you hear a *click* synchronous with the light turning on, and vice-versa when you close it. You attempt to close the door very slowly to find the exact point at which the switch happens." ;pantry
			:title "looking into the cutlery room"
			:dir {:north :crack-egg-room}
			:contents #{:bowl :fork}
			:suspicion -1
	}

	:preperation-room {:desc "The room is uncomfortably empty. The walls are a ghostly white. In the center is a counter, with a sink and a cutting board. It seems like you can wash and cut vegetables in here."
			:title "in the preperation room"
			:dir {:east :pen}
			:contents #{}
			:suspicion 2
	}

	:beat-egg-room {:desc "The room seems designed for an oddly specific task. You can beat some eggs here. You feel anxious."
			:title "in the beat egg room"
			:dir {:west :crack-egg-room}
			:contents #{}
			:suspicion 2
	}
})

(def init-items
 {
   	:raw-egg {:desc "This is a raw egg. You probably want to cook it before eating it."
            :name "a raw egg"
            ; :actions #{:take :crack}
			}
	:cilantro {:desc "A bunch of leafy green cilantro. You should probably wash it, before eating it."
			:name "a bunch of cilantro"
		}
	:onion {:desc "A cute sprig of spring onion. You should probably wash it, before eating it."
			:name "a spring onion"
		}
	:tomato {:desc "A bright red tomato. You should probably wash it, before eating it."
			:name "a tomato"
		}
	:bowl {:desc "A bowl. Perfect for cracking an egg into *hint hint*"
			:name "a bowl"
		}
	:fork {:desc "A fork. Perfect for beating an egg into *hint hint*"
			:name "a fork"
		}
	:cracked-egg {:desc "A bowl with egg in it. You should probably beat it before cooking it. Definetly cook it before eating it though."
			:name "a bowl with egg in it"
		}
	:beat-egg {:desc "A bowl with a cleanly beaten egg in it. You DEFINETLY have to cook it before you eat."
			:name "a bowl with a whisked egg"
		}
	:prepared-vegetables {:desc "A bunch on finely chopped vegetables. Not really sure how you're carrying these"
			:name "a blend of chopped vegetables"
		}
	:omelette {:desc "A delicious omelette. You probably want to sit down at a fancy table and eat this gorgeous result of all your effort."
			:name "a delicious omelette"
		}

;   :chicken {:desc "This is a chicken. You can pet it to get an egg."
;             :name "a chicken" 
;             :actions #{:pet}
;             }          
  })

(def init-adventurer
  {:location :pen
   :inventory #{} ;; :bowl :fork :omelette
   :tick 0
   :seen #{:pen}
   :suspicion 0
   :eggs-recieved 0
   :status :alive
   })



; movement code


; (defn go [state dir]
;   (let [location (get-in state [:adventurer :location])
;         dest ((get-in [:map location :dir] state) dir)]
;     (if (nil? dest)
;       (do (println "You can't go that way.")
;           )
;       (assoc-in state [:adventurer :location] dest))
;   )
; )

(defn quit [state]
	(do
		(println "You become extremely suspicious. You feel controlled. You look all around you, your mind running at a million-miles-an-hour. Finally you look straight ahead at...well...YOU. You punch the clear glass wall and jump out, only to get blown away into dust like at the end of (SPOILER ALERT!) Avengers: Infinity War.")
		(println "GAME OVER")
		(assoc-in state [:adventurer :status] :dead)
))

(def alternateDirs
	{
	:north :north
	:n :north
	:up :north

	:south :south
	:s :south
	:down :south

	:east :east
	:e :east
	:right :east

	:west :west
	:w :west
	:left :west
	}
)

(defn convertDir [dir]

	(if-let [mapDir (get-in alternateDirs [dir])] mapDir :invalid)

)

(defn go [state dir]
  (let [location (get-in state [:adventurer :location])
        dest ((get-in state [:map location :dir]) (convertDir dir) )]
    (if (nil? dest)
      (do (println "\nYou can't go that way.")
	  	; (println state)
          ; (println dir)
          state)

    ;   (if (every? (get-in state [:adventurer :inventory]) (get-in state [:map dest :requires]) ) 
      (do
        (println "\nYou CAN go that way.")
		(print (capitalize (name dest)))
        
		 ;adventurer->suspicion = adventurer->suspicion + room->suspicion
		(if (> (+ (get-in state [:adventurer :suspicion]) (get-in state [:map dest :suspicion])) 10) 
			(quit state)
			(assoc-in
				(assoc-in 
					(assoc-in 
						(assoc-in state [:adventurer :location] dest) 
						[:adventurer :suspicion] 
						(+ (get-in state [:adventurer :suspicion]) (get-in state [:map dest :suspicion]))) 
					[:adventurer :tick] 
					(+ (get-in state [:adventurer :tick]) 1))
				[:adventurer :seen]
			(conj (get-in state [:adventurer :seen]) dest)) 
		
		;adventurer->tick = adventurer->tick + 1
		; (assoc-in state [:adventurer :seen] (conj (get-in state [:adventure :seen]) dest)) ;adventurer->seen = adventurer->seen + dest
	  	

		
		;TODO check if suspicion is too high
)))))



(defn status [state]
  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (str "\nYou are " (-> the-map location :title) ". "))
    (when-not ((get-in state [:adventurer :seen]) location)
      (print (-> the-map location :desc)))
    (update-in state [:adventurer :seen] #(conj % location))))

(defn objects-in-scene [state]

	(println "\nIn this room, the following objects can be interacted with:")
  	(doseq [item (get-in state [:map (get-in state [:adventurer :location]) :contents])] (print item " "))
	state
)

(defn describeDirections [state]

	(println "\nIn this room, these are the following directions you can move in:")
  	(doseq [item (get-in state [:map (get-in state [:adventurer :location]) :dir])] (print item " "))
	state

)

(defn describeState [state]

  ; (status state)

  (let [location (get-in state [:adventurer :location])
        the-map (:map state)]
    (print (-> the-map location :desc)))
	; (println (str "The objects in the scene are " (-> the-map location :contents) ". "))
	(objects-in-scene state)
	(describeDirections state)
  	state

)




(defn describeObject [state object]

  

  (if-let [object (get-in state [:items object])]
           (do
			   (print (get-in object [:desc]))
			   state
		   )
           (do
		   	(print "That object does not exist.")
			state
		   )
  )
  

)

; (defn react
;   "Given a state and a canonicalized input vector, search for a matching phrase and call its corresponding action.
;   If there is no match, return the original state and result \"I don't know what you mean.\""
;   [state input-vector]
;   (loop [idx 0]
;     (if (>= idx (count initial-env)) "I really don't know what you're talking␣
;     ,→about."
;     (if-let [vars (match (initial-env idx) input-vector)]
;     (apply (initial-env (inc idx)) state vars)
;     (recur (+ idx 2))))))

; (defn react [state command]

;   ; (print input-vector)
;   (go state command)

; )

(defn no-understand [state vars] 

  (do 
  
    (print "I don't understand this!")
    state
  )

)

(defn canonicalize
  "Given an input string, strip out whitespaces, lowercase all words, and convert to a vector of keywords."
  [input]
  (def output (split (clojure.string/lower-case input) #"\s+|\.+")) ;regex space or period... lol it actually worked

  ;now need to clean the outputs
  ; (map (fn [input] [str(":" input)]) output)

  ; (map (fn [x] (str ":" x)) output)
  (vec (map (fn [x] (keyword x)) output)))

(defn match [pattern input]
  (loop [pattern pattern
    input input
    vars '()]
    (cond (and (empty? pattern) (empty? input)) (reverse vars)
      (or (empty? pattern) (empty? input)) nil
      (= (first pattern) "@")
      (recur (rest pattern)
      (rest input)
      (cons (first input) vars))
      (= (first pattern) (first input))
      (recur (rest pattern)
      (rest input)
      vars)
      :no-understand nil
    )
  )
)



(defn pick-up [state object]

	; if object is in the current rooms things then pick it up
	(let [objectsInRoom (get-in state [:map (get-in state [:adventurer :location]) :contents])]
		(if (contains? objectsInRoom object)
		
			(do 
				(print (name object) "has been picked up!")
				(if (= object :raw-egg) 
					(update-in (update-in state [:adventurer :eggs-recieved] inc) [:adventurer :inventory] #(conj % object))
					(update-in state [:adventurer :inventory] #(conj % object)))
				
			)

			(do
				(print (format "You dont see any %s around" (name object)))			
				state
			)

		)
	)
)

(defn display-inventory [state]

	(do 
		(print "Inventory: " (get-in state [:adventurer :inventory]))
		state
	)

)

(defn drop [state object]

	(let [objectsInInventory (get-in state [:adventurer :inventory])]
		(if (contains? objectsInInventory object)
		
			(do 
				(print object " has been dropped from the inventory. It hits the ground and disappears into thin air.")
				(update-in state [:adventurer :inventory] #(disj % object))
			)

			(do
				(print "This object is not in your inventory. Not dropped")			
				state
			)

		)
	)	

)

(defn crack-egg [state]

	; if have raw-egg and location crack-egg-room then raw-egg -> cracked egg

	
	(if (= (get-in state [:adventurer :location]) :crack-egg-room) 

		(if (contains? (get-in state [:adventurer :inventory]) :raw-egg)

			

				(do
					(print "Raw eggs have turned into cracked eggs. ")
					;raw-egg -> cracked egg
					(update-in (update-in state [:adventurer :inventory] #(disj % :raw-egg)) [:adventurer :inventory] #(conj % :cracked-egg))
				)

				(do
				
					(print "You do not have any raw eggs to crack.")
					state
				)

			

		)

		(do 
			(print "You are not in the right room to crack an egg.")
			state
		)
	
	)


)

(defn prepare-vegetables [state]

	;prepare-vegetables ;need vegetables (cilantro tomato onion) in preparation-room turns into prepared-vegetables

	(if (every? (get-in state [:adventurer :inventory]) #{:cilantro :tomato :onion})
	
		(if (= (get-in state [:adventurer :location]) :preperation-room) 

			(do
				(print "You now have prepared vegetables.")	  
				(update-in (update-in state [:adventurer :inventory] #(disj % :cilantro :tomato :onion)) [:adventurer :inventory] #(conj % :prepared-vegetables))
			)

			(do
				(print "You are not in the right room to prepare your vegetables.")
				state
			)

		)
		

		(do
		   (print "You don't have all the vegetables required to make prepared vegetables.")
		   state
		)
	
	)

)


(defn beat-egg [state]

	;need beat-egg and prepared-vegetables in kitchen -> omelette

	(if (every? (get-in state [:adventurer :inventory]) #{:prepared-vegetables :cracked-egg :bowl})
	
		(if (= (get-in state [:adventurer :location]) :beat-egg-room) 

			(do
				(print "You now have beat eggs.")	  
				(update-in (update-in state [:adventurer :inventory] #(disj % :prepared-vegetables :cracked-egg)) [:adventurer :inventory] #(conj % :beat-egg))
			)

			(do
				(print "You are not in the right room to beat your eggs.")
				state
			)

		)
		

		(do
		   (print "You don't have all the ingredients and utensils to beat your eggs.")
		   state
		)
	
	)

)



(defn cook-egg [state]
	;need beat-egg in kitchen -> omelette
	(if (every? (get-in state [:adventurer :inventory]) #{:beat-egg})
		(if (= (get-in state [:adventurer :location]) :kitchen) 
			(do
				(print "You now have an OMELETTE!")	  
				(update-in (update-in state [:adventurer :inventory] #(disj % :beat-egg)) [:adventurer :inventory] #(conj % :omelette))
			)

			(do
				(print "You are not in the right room to cook your eggs.")
				state
			)
		)
		
		(do
		   (print "You don't have all the ingredients to cook your eggs.")
		   state
		)
	)
)

(defn eat-egg [state]

	;need omolette fork in dining room -> then set staus to won!

	(if (every? (get-in state [:adventurer :inventory]) #{:omelette :fork :bowl})
	
		(if (= (get-in state [:adventurer :location]) :dining-room) 

			(do
				(print "You now won the game!")
				(assoc-in state [:adventurer :status] :won)
			)

			(do
				(print "You are not in the right room to eat your eggs.")
				state
			)

		)
		

		(do
		   (print "You don't have all the ingredients and utensils to eat your eggs.")
		   state
)))
  

(defn help [state]
	(do 
		(println "You can do any of the following. \n Replace @ with the relevant object. You don't need to type the : before every word")
		(loop [idx 0]
			(if (>= idx (count initial-env)) 
			state
			(do 
				(println (initial-env idx))
				(recur (+ idx 2))
			)
			
    
    	))
	)
)
  
(def initial-env [  
					; [:move "@"] go
					; ["@"] go ;TODO n vs north vs go north
					[:help] help
					[:go "@"] go 
					[:describe :objects] objects-in-scene
                    [:describe] describeState
					[:directions] describeDirections
                    [:describe "@"] describeObject 
                    [:no-understand] no-understand
					[:take "@"] pick-up
					[:drop "@"] drop
					[:look] describeState
					[:examine "@"] describeObject
					[:quit] quit ;TODO	
					[:i] display-inventory
					[:inventory] display-inventory
					[:crack :egg] crack-egg ; if have raw-egg and location crack-egg-room then raw-egg -> cracked egg
					[:prepare :vegetables] prepare-vegetables ;need vegetables (cilantro tomato onion) in preparation-room turns into prepared-vegetables
					[:beat :egg] beat-egg ; need bowl cracked-egg in beat-egg-room -> beat-egg
					[:cook :egg] cook-egg ; need beat-egg and prepared-vegetables in kitchen -> omelette
					[:eat :egg] eat-egg ; need omolette fork in dining room -> then set staus to won!
					[:pick :up "@"] pick-up
					[:pickup "@"] pick-up
					; [:pet :chicken] pet-chicken ; adds eggs to your inventory

                  ])  ;; add your other functions here
					


(defn react
  "Given a state and a canonicalized input vector, search for a matching phrase and call its corresponding action.
  If there is no match, return the original state and result \"I don't know what you mean.\""
  [state input-vector]
  (loop [idx 0]
    (if (>= idx (count initial-env)) 
    (do
      (print "I really don't know what you're talking about.\n")
      state
    )
    (if-let [vars (match (initial-env idx) input-vector)]
    ; (apply (initial-env (inc idx)) state vars)
    (do
      ; (print vars)
      (apply (initial-env (inc idx)) state vars)
    
    )
    (recur (+ idx 2))
    
    
    ))))

(defn -main
  "Initialize the adventure"
  [& args]
  (loop [local-state {:map init-map :adventurer init-adventurer :items init-items}]
  
  
	; restucture the print ln and move to a do loop with the recur so that you 
    

          (do 
          ; (print ( (get-in local-state [:map (get-in local-state [:adventurer :location]) :dir]) :north) )
          ; (recur (go local-state (canonicalize command))) 
		
			(if (= (get-in local-state [:adventurer :status]) :dead)

				(do 
					(println "THE GAME IS OVER. YOU DIED / EXITED.")
					(println (get-in local-state [:adventurer :tick]) " moves taken.")
				)

				(if (= (get-in local-state [:adventurer :status]) :won) 
					(do 
						(print "\nYOU WON in " (get-in local-state [:adventurer :tick]) " moves.")
					)
					(do
						(status local-state)
						(println "\nWhat do you want to do?") 
						(recur (react local-state (canonicalize (read-line))))
					)

				)

))))





; https://clojuredocs.org/clojure.set/rename-keys for renaming keys
