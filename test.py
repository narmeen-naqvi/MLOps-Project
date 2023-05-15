import base64
import io
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
    # Convert the NumPy array back to PIL Image
    processed_image = Image.fromarray(img)
    # Normalize the image
    x = np.array(processed_image).astype('float32') / 255.0
    # Add an extra dimension to match the model input shape
    x = np.expand_dims(x, axis=0)
    return x

@app.route('/', methods=['GET', 'POST'])
def handle_request():
    return "Flask Server & Android are connected"

@app.route('/upload', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST':
        file = request.files['image']
        # Open the image file with Pillow
        image = Image.open(file)
        # buffered = BytesIO()
        # image.save(buffered, format="JPEG")
        # buffered.seek(0)
        # Process the image using the process_image function
        processed_image = process_image(image)
        # Convert the processed image back to a PIL Image object
        processed_pil_image = Image.fromarray((processed_image[0] * 255).astype(np.uint8))

        # Convert the processed image to a byte stream
        buffered = BytesIO()
        processed_pil_image.save(buffered, format="JPEG")
        img_str = base64.b64encode(buffered.getvalue()).decode('utf-8')
        # Generate the HTML content that displays the uploaded and processed images
        image_bytes = file.read()
        html = '''
        <h1>Uploaded Image:</h1>
        <img src="data:image/png;base64,{}"><br><br>
        <h1>Processed Image:</h1>
        <img src="data:image/png;base64,{}">
    '''.format(base64.b64encode(image_bytes).decode('utf-8'), img_str)
        # Return a JSON response with the result
        result = {'message': 'Image processed successfully!'}
        #return jsonify(result)

        # do something with the uploaded file
        # return ''''Image uploaded successfully'
        #     <h1>Uploaded Image:</h1>
        #     <img src="data:image/png;base64,{}">
        # '''.format(base64.b64encode(image_bytes).decode('utf-8'))

        # Create a response object that contains both the JSON data and the HTML content
        response = make_response(jsonify(result))
        response.content_type = 'text/html'
        response.data += html.encode('utf-8')
        return response
    else:
        # handle GET request
        return 'Upload page'
    # '''
    #         <form action="/upload" method="POST" enctype="multipart/form-data">
    #             <input type="file" name="image">
    #             <input type="submit" value="Upload">
    #         </form>
    #     '''

if __name__=="__main__":
    app.run(host="0.0.0.0",port=5000,debug=True)