package com.riderwise.ai;

import android.graphics.Bitmap;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.*;
import java.util.List;

public class OcrProcessor {
    public interface Callback { void onResult(List<Booking> bookings); }
    private final TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    private final BookingExtractor extractor = new BookingExtractor();
    public void process(Bitmap bitmap, Callback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(text -> callback.onResult(extractor.extract(text)))
                .addOnFailureListener(e -> callback.onResult(java.util.Collections.emptyList()));
    }
    public void close() { recognizer.close(); }
}
