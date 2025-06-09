import { useState } from "react";
import './App.css';

function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");

  const sendMessage = async () => {
    if (!input.trim()) return;
  
    const newMessages = [...messages, { sender: "user", text: input }];
    setMessages(newMessages);
    setInput("");
  
    try {
      const response = await fetch(process.env.REACT_APP_API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt: input })
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error?.message || "API Error");
      }
  
      const data = await response.json();
      setMessages([...newMessages, { sender: "bot", text: data.response }]);
    } catch (err) {
      setMessages([
        ...newMessages,
        { sender: "bot", text: `❌ Error: ${err.message}` }
      ]);
    }
  };
  

  return (
    <div className="chat-container">
      <h1>NovaGPT</h1>
      <div className="chat-box">
        {messages.map((msg, idx) => (
          <div key={idx} className={`message ${msg.sender}`}>
            <span>{msg.text}</span>
          </div>
        ))}
      </div>
      <div className="input-area">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
          placeholder="Ask anything..."
        />
        <button class="" onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}

export default App;
