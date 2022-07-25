package com.example.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddBookmarkActivity extends AppCompatActivity {
    String FILENAME; // 파일명
    EditText edit1; // 출발지
    EditText edit2; // 목적지
    Button btn_voice;
    Boolean edit1set = false;
    SpeechRecognizer mRecognizer;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbookmark);
        Boolean edit1set = false;
        edit1 = (EditText) findViewById(R.id.start); // EditText id를 받아옴
        edit2 = (EditText) findViewById(R.id.end);

        Button okBtn = (Button) findViewById(R.id.addButton);
        Button btn_voice = (Button) findViewById(R.id.btn_voice);

        // RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정

        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(AddBookmarkActivity.this);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(intent);
            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() { // 추가하기 버튼 클릭 이벤트
            @Override
            public void onClick(View v) {
                try {
                    FILENAME = setFilename(); // setFilename()을 통해 파일명 설정
                    if (!edit1.getText().toString().equals("") && !edit2.getText().toString().equals("")) {
                        // 값이 없으면 저장하지 않음
                        FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                        // 파일에 쓰기 위해 엶
                        fos.write((edit1.getText().toString() + "->" + edit2.getText().toString()).getBytes());
                        // 출발지->목적지 형태로 내용을 저장
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(),"음성인식 시작",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (int i=0; i < matches.size(); i++) {
                if (edit1set == false) {
                    edit1.setText(matches.get(i));
                }
                else{
                    edit2.setText(matches.get(i));
                }
            }
            edit1set = true;
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };
    @Override
    public String[] fileList() { // 파일 목록을 반환하는 함수
        return super.fileList();
    }

    public String setFilename() { // 파일명 설정하는 함수
        int tmp = 0;
        int max = 0; // 있는 파일 명 중 가장 큰 번호 @ (map + @에서)
        if (fileList().length != 0) {
            for (int i = 0; i < fileList().length; i++) {
                tmp = Integer.parseInt(fileList()[i].substring(3)); // 파일명에서 숫자만 저장
                if (max < tmp) // 값을 하나씩 비교해서 최대값을 구함
                    max = tmp;
            }
        } else // 파일이 하나도 없으면 0번부터 시작
            max = -1;
        return "map" + String.valueOf(max + 1);
    }
}   