# from flask import Flask, request
# import werkzeug

# app = Flask(__name__)

# @app.route('/', methods=['GET', 'POST'])
# def handle_request():
#     return "Flask Server & Android are connected"

# @app.route('/upload', methods=['GET', 'POST'])
# def upload():
#     imagefile = request.files['image']
#     filename = werkzeug.utils.secure_filename(imagefile.filename)
#     print("\nReceived image File name : " + imagefile.filename)
#     imagefile.save(filename)
#     return "Image Uploaded Successfully"

# if __name__ == '__main__':
#     app.run(host='0.0.0.0', port=5000,debug=True)

import base64
from flask import Flask, request, jsonify, make_response
from PIL import Image, ImageFilter
from io import BytesIO
import json
from keras.models import model_from_json
import cv2
import numpy as np

app = Flask(__name__)

# Load the model architecture from the JSON file
with open('model_idraak.json', 'r') as f:
    model_architecture = json.load(f)
model = model_from_json(json.dumps(model_architecture))

# Load the model weights from the h5 file
model.load_weights('model_idraak.h5')

# lookup table
urdu_alphabet = ['ء', 'ا', 'آ', 'ب', 'ت', 'ث', 'ج', 'ح', 'خ', 'د', 'ذ', 'ر', 'ز', 'س', 'ش', 'ص', 'ض', 'ط', 'ظ', 'ع', 'غ', 'ف', 'ق', 'ل', 'م', 'ن', 'هـ', 'و', 'ٹ', 'پ', 'چ', 'ڈ', 'ڑ', 'ژ', 'ک', 'گ', 'ہ', 'ی', 'ے']

def process_image(image):
    # Convert to grayscale
    gray_img = image.convert('L')
    # Apply Gaussian blur
    blur_img = gray_img.filter(ImageFilter.GaussianBlur(radius=2))
    # Convert the PIL Image to NumPy array
    img_array = np.array(blur_img)
    # Convert to grayscale using OpenCV
    gray_img = cv2.cvtColor(img_array, cv2.COLOR_GRAY2BGR)
    gray_img = cv2.cvtColor(gray_img, cv2.COLOR_BGR2GRAY)  # Convert to grayscale
    # Apply adaptive thresholding
    th3 = cv2.adaptiveThreshold(gray_img, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, 11, 2)
    # Apply Otsu's thresholding
    ret, img = cv2.threshold(th3, 1, 255, cv2.THRESH_BINARY_INV+cv2.THRESH_OTSU)
    # Resize the image
    img = cv2.resize(img, (128, 128))
    # Mirroring image left to right for backcamera
    img = cv2.flip(img, 1)
    # Convert the NumPy array back to PIL Image
    processed_image = Image.fromarray(img)
    # Normalize the image
    x = np.array(processed_image).astype('float32') / 255.0
    # Add an extra dimension to match the model input shape
    x = np.expand_dims(x, axis=-1)
    return x

def predict_letter(ix):
    pred_index = np.argmax(model.predict(ix))
    pred_letter = urdu_alphabet[pred_index]
    return pred_letter

@app.route('/', methods=['GET', 'POST'])
def handle_request():
    return "Flask Server & Android are connected"

@app.route('/upload', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST':
        if 'image' not in request.files:
            return jsonify({'error': 'No image uploaded'}), 400
        
        file = request.files['image']
        # Open the image file with Pillow
        image = Image.open(file)
        # Process the image using the process_image function
        processed_image = process_image(image)
        # Convert the processed image back to a PIL Image object
        processed_pil_image = Image.fromarray((processed_image[0] * 255).astype(np.uint8))
        # Predict the letter in the image
        prediction = predict_letter(processed_image.reshape(1, 128, 128))
        # Convert the images to base64-encoded strings
        buffered = BytesIO()
        image.save(buffered, format='JPEG')
        uploaded_image_base64 = base64.b64encode(buffered.getvalue()).decode('utf-8')

        # processed_image_array = np.array(processed_image)
        # _, processed_image_encoded = cv2.imencode('.jpeg', np.uint8(processed_image_array))
        # processed_image_base64 = base64.b64encode(processed_image_encoded).decode('utf-8')

        #its this part
        # processed_buffered = BytesIO()
        # processed_pil_image.save(processed_buffered, format='JPEG')
        # processed_image_base64 = base64.b64encode(processed_buffered.getvalue()).decode('utf-8')
        
        
        response = {
            'message': 'Image processed successfully!',
            #'uploaded_image': uploaded_image_base64,
            #'processed_image': processed_image_base64,
            'prediction': prediction
        }
        return jsonify(response), 200

    return jsonify({'error': 'Invalid request method'}), 405

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

