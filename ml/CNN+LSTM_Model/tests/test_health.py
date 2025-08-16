import importlib
import types
import os

# Import the api module
api = importlib.import_module('ml.CNN+LSTM_Model.api'.replace('+', '_')) if False else None  # placeholder to avoid syntax

# Because folder name has a plus sign, dynamic import via package path is awkward.
# We instead adjust sys.path and import directly.
import sys
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PARENT_DIR = os.path.dirname(SCRIPT_DIR)
if PARENT_DIR not in sys.path:
    sys.path.insert(0, PARENT_DIR)

import api  # now import the api.py in the CNN+LSTM_Model directory

# For speed in CI avoid loading the heavy model; ensure model variable stays None.
# Health endpoint will attempt lazy load; that's acceptable but may fail if model file absent.
# We patch load_emotion_model to avoid heavy TensorFlow load during quick tests.
api.load_emotion_model = lambda: True


def test_health_endpoint(monkeypatch):
    client = api.app.test_client()
    resp = client.get('/health')
    assert resp.status_code in (200, 503)
    data = resp.get_json()
    assert 'status' in data

