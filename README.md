# Recursive Generative Art (Java + Processing)

This project implements a **recursion-based generative art system** using **Java** and the **Processing framework**.  
Each execution produces a **unique visual composition** by combining recursive spatial subdivision with randomized drawing rules.

The goal of the project is to demonstrate how **classical computer science algorithms** (recursion, randomness, termination conditions) can be used to generate complex and expressive visual artifacts.

---

## ✨ Key Features

- Recursive algorithm for spatial subdivision  
- Randomized generation: different artwork on every run  
- Deterministic reproduction via random seeds  
- Built with pure Java (no Processing IDE required)  
- High-resolution image export (PNG)

---

## 🧠 Core Algorithm

The artwork is generated using a **recursive region-splitting algorithm**:

1. Start with a rectangular canvas region.
2. Recursively subdivide the region into smaller rectangles.
3. At each recursion step:
   - Choose a split direction (horizontal or vertical) probabilistically.
   - Choose a split ratio randomly within safe bounds.
4. Recursion terminates when:
   - A maximum depth is reached, or
   - The region becomes smaller than a predefined threshold.
5. Leaf regions are rendered using randomly selected visual motifs:
   - Noisy circles
   - Arcs
   - Organic blobs
   - Hatching patterns

This approach transforms **simple local rules** into a **globally complex composition**, a common principle in generative systems.

---

## 🎲 Randomness & Reproducibility

- Each execution uses a random seed, ensuring a different output every time.
- The seed value is displayed on the canvas.
- Reusing the same seed reproduces the exact same artwork.

### Keyboard Controls

- **R** – regenerate with a new random seed  
- **S** – save the current artwork as an image  

---

## 🛠️ Technology Stack

- **Language:** Java  
- **Graphics Framework:** Processing (core)  
- **Build System:** Maven  

---

## 🖼️ Output

- Resolution: `1200 × 900` (configurable)
- Output format: PNG
- Style: abstract, geometric, organic
- Each run produces a unique composition

---

## 📄 License

This project is provided for educational purposes.
