# Importing Libraries
import os
import string

# Creating directories

if not os.path.exists("dataSet"):
    os.makedirs("dataSet")

if not os.path.exists("dataSet/trainingData"):
    os.makedirs("dataSet/trainingData")

if not os.path.exists("dataSet/testingData"):
    os.makedirs("dataSet/testingData")

# Making folder  0 (for input into folders) in the training and testing data folders 
for i in range(0):
    if not os.path.exists("dataSet/trainingData/" + str(i)):
        os.makedirs("dataSet/trainingData/" + str(i))

    if not os.path.exists("dataSet/testingData/" + str(i)):
        os.makedirs("dataSet/testingData/" + str(i))

# creating a list of Urdu alphabets
urdu_list = ['آ', 'ا', 'ب', 'پ', 'ت', 'ٹ', 'ث', 'ج', 'چ', 'ح', 'خ', 'د', 'ڈ', 'ذ', 'ر', 'ڑ', 'ز', 'ژ', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ک', 'گ', 'ل', 'م', 'ن', 'و', 'ہ','هـ', 'ی', 'ے']
# 'کتاب', 'میری', 'آپ', 'کون', 'کیا', 'کرتا', 'ہوں', 'آج', 'کل', 'انسان', 'سورج', 'چاند', 'سکول', 'کالج', 'یونیورسٹی'

# Making folders for each letter of the Urdu Alphabet in the training and testing data folders 
 
for i in urdu_list:
    if not os.path.exists("dataSet/trainingData/" + i):
        os.makedirs("dataSet/trainingData/" + i)
    
    if not os.path.exists("dataSet/testingData/" + i):
        os.makedirs("dataSet/testingData/" + i)
