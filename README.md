# 📖 VerseSearch-Java

VerseSearch-Java is a project I built as part of my CSIS 312 semester assignment. It's a desktop application written in Java that lets users search for Bible verses using the Bible API. The idea came from wanting something simple but powerful — something I could actually use and maybe even share with others.

---

## ✨ Features

- 🔍 **Search Any Verse**: Just type in something like `John 3:16` or even a range like `Psalm 23:1-4`, and it pulls the verse(s) from the Bible API.
- 📚 **Search History**: It keeps track of what you’ve looked up.
- 💾 **Save to File**: You can save your search history to a text file.
- 🌗 **Dark Mode**: You can toggle between light and dark modes.
- 🧑‍💻 **Sleek Interface**: I used Java Swing and made the layout clean and easy to use.

---

## 🚀 Getting Started

### 📦 Requirements
- Java JDK 17 or newer
- Internet connection (to get verses from the API)
- [json-20210307.jar](https://repo1.maven.org/maven2/org/json/json/20210307/json-20210307.jar) (used to handle the API responses)

### 🧩 How to Run

1. Download or clone the repo:
```bash
git clone https://github.com/yourusername/VerseSearch-Java.git
cd VerseSearch-Java
```

2. Make sure `json-20210307.jar` is in your project directory (or `lib/` folder if using an IDE).

3. Compile and run:
```bash
javac -cp .:json-20210307.jar VerseSearchApp.java
java -cp .:json-20210307.jar VerseSearchApp
```

> ⚠️ If you're on Windows, change the colon `:` to a semicolon `;` in the classpath.

---

## 🔌 API Reference
This app uses the [Bible API](https://bible-api.com/) — no login or key needed.
- For a single verse: `https://bible-api.com/john+3:16`
- For a range: `https://bible-api.com/psalm+23:1-4`

It currently supports only KJV, which works fine for this project.

---

## 🗃️ File Structure
```
VerseSearch-Java/
├── VerseSearchApp.java
├── json-20210307.jar
├── verse_search_history.txt (this gets created when you save history)
├── README.md
```


---

## 🛠 Development Notes
- Built from scratch using Java Swing.
- All the logic is in one class, but it’s organized and readable.
- Easy to add more features later like dropdowns for translations or offline storage.

---

## ✝️ Purpose
I wanted to build something that reflects both my technical growth and my faith. I use the Bible regularly, and having a quick way to look up verses (without ads or distractions) is genuinely useful. This app helped me connect programming with something that matters to me personally.

---

## 📄 License
MIT License — basically, feel free to use or modify it, just keep the credits.

---

## 🙌 Credits
- [Bible API](https://bible-api.com/) for the verse content.
- `org.json` for helping with the response parsing.
- Inspiration came from mixing coding practice with daily devotion.

---

If you want to use this, fork it, or improve it — go ahead. I'd love to see what others might do with it.

> _"Your word is a lamp to my feet and a light to my path." – Psalm 119:105_
