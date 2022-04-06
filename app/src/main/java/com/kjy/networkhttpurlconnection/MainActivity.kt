package com.kjy.networkhttpurlconnection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kjy.networkhttpurlconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 요청 버튼 이벤트 구현
        binding.buttonRequest.setOnClickListener {
            // 버튼 클릭시 네트워크 작업을 요청하고 이를 백그라운드에서 처리.
            CoroutineScope(Dispatchers.IO).launch {
                // 네트워크 관련 코드는 예외로 치명적인 오류가 발생할 수 있기 때문에 try-catch 문으로 감싸고
                // e.printStackTrace 메서드로 예외 발생시 로그를 출력하는 역할을 함.
                try {
                    // 주소를 가져왔을 때 https로 시작하지 않을시 https:// 를 붙여줌.
                    var urlText = binding.editUrl.text.toString()
                    if (!urlText.startsWith("https")) {
                        urlText = "https://${urlText}"
                    }

                    // 주소를 URL 객체로 변환하고 변수에 저장.
                    // .net import
                    val url = URL(urlText)
                    /*
                openConnection 메서드로 서버와의 연결을 생성. openConnection에는 URLConnection 이라는
                추상 메서드가 반환되기 때문에 실제 구현 클래스로 형변환 작업이 필요함.
                 */
                    val urlConnection = url.openConnection() as HttpURLConnection
                    // 연결된 커넥션에 요청 방식을 설정.
                    urlConnection.requestMethod = "GET"         // 지정한 Url 리소스를 요청하는 방식.

                    // 응답을 확인하여 정상이면 데이터를 처리함.
                    if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                        // 입력 스트림(데이터를 읽어옴)을 연결하고 버퍼에 담아서 데이터를 읽을 준비를 함.
                        val streamReader = InputStreamReader(urlConnection.inputStream)
                        val buffered = BufferedReader(streamReader)

                        // 한 줄씩 읽은 데이터를 content 변수에 저장.
                        val content = StringBuilder()
                        while (true) {
                            val line = buffered.readLine() ?: break
                            content.append(line)
                        }

                        // 사용한 스트림과 커넥션을 모두 해제
                        buffered.close()
                        urlConnection.disconnect()

                        // 화면의 텍스트뷰에 content 변수에 저장된 값을 입력. UI에 세팅하는 것은 Main 디스패치에서 해야함.
                        launch(Dispatchers.Main) {
                            binding.textContent.text = content.toString()
                        }

                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}