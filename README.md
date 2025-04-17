# 📖 VerseSearch-Java

VerseSearch-Java is a modern desktop application built with Java Swing that allows users to search Bible verses by reference, explore ranges of Scripture, and manage verse history through a beautiful, responsive GUI. It uses the public [Bible API](https://bible-api.com/) to retrieve accurate Scripture content (KJV only).

---

## ✨ Features

- 🔍 **Verse Lookup**: Retrieve Bible verses by entering references like `John 3:16` or `Psalm 23:1-3`.
- 🗂 **Search History**: View past lookups and revisit them anytime.
- 💾 **Save to File**: Export your search history to a `.txt` file.
- 🌗 **Dark Mode**: Toggle light and dark themes for a comfortable reading experience.
- 🎨 **Modern UI**: Sleek layout with styled buttons, borders, and fonts.

---

## 🚀 Getting Started

### 📦 Requirements
- Java JDK 17 or newer
- Internet connection (for API access)
- [json.org](https://mvnrepository.com/artifact/org.json/json) library: `json-20210307.jar`

### 🧩 Setup Instructions

1. Clone the repo:
```bash
git clone https://github.com/yourusername/VerseSearch-Java.git
cd VerseSearch-Java
```

2. Add the `json-20210307.jar` to your classpath (or `lib` folder in IDE).

3. Compile and run:
```bash
javac -cp .:json-20210307.jar VerseSearchApp.java
java -cp .:json-20210307.jar VerseSearchApp
```

> ⚠️ On Windows, replace `:` with `;` in the classpath.

---

## 🔌 API Reference
This app uses the [Bible API](https://bible-api.com/) — no key required.
- Single verse: `https://bible-api.com/john+3:16`
- Range: `https://bible-api.com/psalm+23:1-3`
- Only KJV translation supported

---

## 🗃️ File Structure
```
VerseSearch-Java/
├── VerseSearchApp.java
├── json-20210307.jar
├── verse_search_history.txt (generated)
├── README.md
```

> You can optionally include screenshots later in a `screenshots/` folder.

---

## 🛠 Development Notes
- Built using Java Swing (no FXML)
- Fully self-contained single-file GUI
- Easily extensible with translation dropdowns or local Bible files

---

## ✝️ Purpose
This project was created for educational and faith-based purposes. It's a demonstration of how programming can be used to explore and reflect on Scripture, and how tech can serve the Kingdom.

---

## 📄 License
MIT License

---

## 🙌 Credits
- [Bible API](https://bible-api.com/)
- JSON.org Java Library
- Verse styling inspired by community UI best practices

---

Feel free to fork, extend, or integrate this into your own devotional tools!

> _"Your word is a lamp to my feet and a light to my path." – Psalm 119:105_
