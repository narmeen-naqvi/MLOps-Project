import numpy as np
import cv2
import os

mode = 'preprocessingImages'
directory =  mode + '/'

minValue = 70
def func(path):    
    frame = cv2.imread(path)
    
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    blur = cv2.GaussianBlur(gray,(5,5),2)

    th3 = cv2.adaptiveThreshold(blur,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C,cv2.THRESH_BINARY_INV,11,2)
    ret, res = cv2.threshold(th3, minValue, 255, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
    return res

a=directory+"temp/"

for i in os.listdir(a):
    x=func(a+i)
    cv2.imwrite(a+i, x)
