# Le but de ce script est de convertir toutes les images webp d'un dossier en 
# gif

import os
import subprocess
import sys

def find_all_webp():
    # Trouve tous les fichiers webp dans le dossier courant
    # Retourne une liste de tous les fichiers webp
    files = os.listdir()
    webp_files = []
    for file in files:
        if file.endswith(".webp"):
            webp_files.append(file)
    return webp_files

def convert_webp_to_gif(webp_files):
    """Convertit tous les fichiers webp en gif avec magick. Rajoute le chemin
    absolu du fichier converti dans une liste.

    Args:
        webp_files (list): Liste de tous les fichiers webp à convertir.
    """
    # # magick convert -format gif My_anim.webp animation.gif
    # for file in webp_files:#
    #     gif_file = os.path.splitext(file)[0] + ".gif"
    #     subprocess.run(["magick", "convert", "-format", "gif", file, gif_file])
    # On multi thread le processus de conversion
    processes = []
    for file in webp_files:
        # On rajoute le chemin absolu du fichier converti dans une liste
        # pour l'utiliser plus tard
        gif_file = os.path.splitext(file)[0] + ".gif"
        processes.append(subprocess.Popen(["magick", "convert", "-format", "gif", file, gif_file]))
    # On attend que tous les processus se terminent
    for process in processes:
        process.wait()
        


        
    


if __name__ == "__main__":
    webp_files = find_all_webp()
    convert_webp_to_gif(webp_files)