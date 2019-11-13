"""
Main driver for the program, really just a jumping off point for now.
"""

import sys
import random

import pygame

pygame.init()

size = width, height = 1020, 780
BLACK = 0, 0, 0
GREEN = 0, 255, 0
RED   = 255, 0, 0

screen = pygame.display.set_mode(size)
myfont = pygame.font.SysFont("monospace", 13, True)

vehicles = []

#set in px, cars will be in a 2:1 ratio
carLength = 60

#these are imortant to figuring out min safe following distance
#the higher the deceleraion the closer they will follow
maxAcceleration = 0.01
maxDeceleration = 0.03

#needs a number of cars for each lane:
#aka. carsPerLane.length == numLanes
carsPerLane = [5, 4, 6, 4, 3, 4]

#maxLane speed is set by the lane speed 
#the speed will/can go over this speed (when a car is looped)
laneSpeed =   [3, 3, 3, 3, 3, 3]

#loop just creates all the cars
for laneNum in xrange(0, len(carsPerLane)):
    #random x and y speed for the lane of cars
    xspeed = random.randint(1, laneSpeed[laneNum])
    yspeed = 0
    lane = []
    for position in xrange(0, carsPerLane[laneNum]):
        #chosing a random car image for each car
        sprite = "car%d.png" % (random.randint(1, 6))

        #getting the sprite and scaling it to a good size (usually in a 1x2 ratio)
        img = pygame.transform.scale(pygame.image.load(sprite), (carLength, carLength/2))

        #making a car object
        car = dict({"sprite":img, "hitbox":img.get_rect(), "speed":[xspeed, yspeed], "stopped": False})

        #moving car into its lane/ starting position (1 car lengh in front of eachother)
        car["hitbox"] = car["hitbox"].move(2*carLength*position, laneNum*(height/len(carsPerLane)))
        lane.append(car)
        pass
    vehicles.append(lane)
    pass

while 1:
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            sys.exit()
        if event.type == pygame.MOUSEBUTTONUP:
            for lane in vehicles:
                for car in lane:
                    if car["hitbox"].collidepoint(event.pos):
                        car["stopped"] = not car["stopped"]

    #necessary first step
    #delete it if you wanna see some weird shit
    screen.fill(BLACK)
    followingCar = None

    #processing each lane at a time, keeps things organized and makes
    #it easier to find nexts an previouses
    laneNum = 0
    for lane in vehicles:
        #car has:
        #    hitbox (car["hitbox"])
        #    sprite (car["sprite"])
        #    speed: car["speed"] = [xspeed, yspeed]
        carFollowing = lane[0]

        #this will accelerate all cars in the lane to maxStartSpeed, maintaining a minimum following distance
        for car in reversed(lane): 
            #finds distance to the car infront
            distance = carFollowing["hitbox"].left - car["hitbox"].right
            #this is only needed to calculate distance when it wraps around
            if distance < 0:
                distance = carFollowing["hitbox"].left + (width - car["hitbox"].right) 

            #THIS NEEDS TO BE ADJUSTED
            #I used vf^2 = vi^2 + 2ad, solved for d. 
            #Final velocity should be 0 so d = vi^2/(2a)
            minimumFollowingDistance = (car["speed"][0]**2)/(2*maxDeceleration)


            #the main speed controling algorithm
            if (distance > minimumFollowingDistance) and not car["stopped"]:
                #safe distance so accelerate to the lane speed
                color = GREEN
                if(car["speed"][0] <= laneSpeed[laneNum]):
                    car["speed"][0] += maxAcceleration
                else:
                    #if its  speeding itll slowly slow down
                    car["speed"][0] -= maxAcceleration/100
            else:
                #too close, decelerate
                color = RED
                car["speed"][0] -= maxDeceleration
            
            #no reversing
            if car["speed"][0] <= 0:
                car["speed"][0]  = 0.0

            #draws the follow line
            pygame.draw.line(screen, color,
                                 car["hitbox"].midright,
                                [car["hitbox"].midright[0] + minimumFollowingDistance - 10,
                                 car["hitbox"].midright[1]])


            #bound checking, also this is where all the movement code will go
            car["hitbox"] = car["hitbox"].move(car["speed"])
            if car["hitbox"].left < 0 or car["hitbox"].right > width:
                car["hitbox"] = car["hitbox"].move(-width+carLength, 0)
                #to show they slow down, just make it so they catch up and need to slow down
                car["speed"][0] += 1

            if car["hitbox"].top < 0 or car["hitbox"].bottom > height:
                car["speed"][1] = -car["speed"][1]


            #actually drawing the car on the frame
            screen.blit(car["sprite"], car["hitbox"])

            #ui information on car speed
            speed= myfont.render("{:.3}".format(car["speed"][0]), 1, color)
            textBG = speed.get_rect()
            textBG.center = car["hitbox"].center
            pygame.draw.rect(screen, BLACK, textBG)
            screen.blit(speed, textBG.topleft)
            
            #keeping track of cars in the lane
            carFollowing = car
        laneNum += 1
    pygame.display.flip()


