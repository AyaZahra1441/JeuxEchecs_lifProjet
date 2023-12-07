package jeuxEchecs;


import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

enum TypePiece {
    ROI, REINE, TOUR, CAVALIER, FOU, PION
}

class Piece {

    private ImageIcon image;
    private TypePiece type;
    private boolean estBlanc;

    public Piece(ImageIcon image, TypePiece type, boolean estBlanc) {
        this.image = image;
        this.type = type;
        this.estBlanc = estBlanc;
    }

    public ImageIcon getImageIcon() {
        return this.image;
    }

    public boolean estBlanc() {
        return this.estBlanc;
    }
    

    public TypePiece getType() {
        return this.type;
    }


    public boolean mouvementValide(Point depart, Point arrivee, Piece[][] plateau) {
        switch (this.type) {
            case PION:
                return mouvementValidePion(depart, arrivee, plateau);
            case TOUR:
                return mouvementValideTour(depart, arrivee, plateau);
            case FOU:
                return mouvementValideFou(depart, arrivee, plateau);
            case REINE:  
                return mouvementValideReine(depart, arrivee, plateau);
            case CAVALIER:  
                return mouvementValideCavalier(depart, arrivee, plateau);
            case ROI:
                return mouvementValideRoi(depart, arrivee , plateau);
            default:
                return false;
        }
    }

    private boolean mouvementValidePion(Point depart, Point arrivee, Piece[][] plateau) {
        int direction = estBlanc ? -1 : 1;

        // 1 case en avant
        if (arrivee.x == depart.x && arrivee.y == depart.y + direction) {
            return plateau[arrivee.y][arrivee.x] == null;
        }

        // deux cases en avant
        if (arrivee.x == depart.x && arrivee.y == depart.y + 2 * direction) {
            return (estBlanc ? depart.y == 6 : depart.y == 1) && plateau[arrivee.y][arrivee.x] == null;
        }

        // Capture diagonale
        if (Math.abs(arrivee.x - depart.x) == 1 && arrivee.y == depart.y + direction) {
            return plateau[arrivee.y][arrivee.x] != null && plateau[arrivee.y][arrivee.x].estBlanc() != this.estBlanc();
        }

        return false;
    }

    public boolean mouvementValideTour(Point depart, Point arrivee, Piece[][] plateau) {// Vérifier si le mouvement est horizontal ou vertical
    	if (depart.x == arrivee.x) {
    	    // Mouvement vertical
    	    int step = (depart.y < arrivee.y) ? 1 : -1;
    	    for (int y = depart.y + step; y != arrivee.y; y += step) {
    	    	if (y < 0 || y >= 8) {
    	            break;
    	        }
    	        if (plateau[y][depart.x] != null) {
    	            
    	            if (y == arrivee.y && plateau[y][depart.x].estBlanc() == plateau[depart.y][depart.x].estBlanc()) {
    	                return false;
    	            }
    	            if (y != arrivee.y) {
    	                return false;
    	            }
    	        }
    	    }
    	    return plateau[arrivee.y][arrivee.x] == null || plateau[arrivee.y][arrivee.x].estBlanc() != this.estBlanc;
    	} else if (depart.y == arrivee.y) {
    	    // Mouvement horizontal
    	    int step = (depart.x < arrivee.x) ? 1 : -1;
    	    for (int x = depart.x + step; x != arrivee.x; x += step) {
    	        if (plateau[depart.y][x] != null) {
    	            return false; 
    	        }
    	    }
    	    return plateau[arrivee.y][arrivee.x] == null || plateau[arrivee.y][arrivee.x].estBlanc() != this.estBlanc;
    	}
    	return false;
}
    
    private boolean mouvementValideFou(Point depart, Point arrivee, Piece[][] plateau) {
        int deltaX = arrivee.x - depart.x;
        int deltaY = arrivee.y - depart.y;
        
        if (Math.abs(deltaX) != Math.abs(deltaY)) {
            return false;
        }
        if ((depart.x + depart.y) % 2 != (arrivee.x + arrivee.y) % 2) {
            return false;
        }
        int xStep = (deltaX > 0) ? 1 : -1;
        int yStep = (deltaY > 0) ? 1 : -1;

        int x, y;
        for (x = depart.x + xStep, y = depart.y + yStep; x != arrivee.x && y != arrivee.y; x += xStep, y += yStep) {
            if (x < 0 || x >= 8 || y < 0 || y >= 8) {
                return false;
            }
            if (plateau[y][x] != null) {
                return false; 
            }
        }
        if (plateau[arrivee.y][arrivee.x] != null) {
            return plateau[arrivee.y][arrivee.x].estBlanc() != this.estBlanc();
        }

        return true;
    }

   
    private boolean mouvementValideReine(Point depart, Point arrivee, Piece[][] plateau) {
        if ((depart.x == arrivee.x || depart.y == arrivee.y) || 
            (Math.abs(depart.x - arrivee.x) == Math.abs(depart.y - arrivee.y))) {
            if (depart.x == arrivee.x || depart.y == arrivee.y) {
                return mouvementValideTour(depart, arrivee, plateau) &&
                       (plateau[arrivee.y][arrivee.x] == null || 
                        plateau[arrivee.y][arrivee.x].estBlanc() != plateau[depart.y][depart.x].estBlanc());
            }
            if (Math.abs(depart.x - arrivee.x) == Math.abs(depart.y - arrivee.y)) {
                return mouvementValideFou(depart, arrivee, plateau) &&
                       (plateau[arrivee.y][arrivee.x] == null || 
                        plateau[arrivee.y][arrivee.x].estBlanc() != plateau[depart.y][depart.x].estBlanc());
            }
        }
        return false;
    }

    
    
    private boolean mouvementValideCavalier(Point depart, Point arrivee, Piece[][] plateau) {
        int deltaX = Math.abs(depart.x - arrivee.x);
        int deltaY = Math.abs(depart.y - arrivee.y);

        if ((deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2)) {
            return plateau[arrivee.y][arrivee.x] == null || plateau[arrivee.y][arrivee.x].estBlanc() != this.estBlanc;
        }

        return false;
    }



    private boolean mouvementValideRoi(Point depart, Point arrivee, Piece[][] plateau) {
        int deltaX = arrivee.x - depart.x;
        int deltaY = arrivee.y - depart.y;
        
        if (Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1) {
            if (plateau[arrivee.y][arrivee.x] == null || plateau[arrivee.y][arrivee.x].estBlanc() != plateau[depart.y][depart.x].estBlanc()) {
                return true;
            }
        }
        return false;
    }
    
    public List<Point> getMouvementsValides(Point position, Piece[][] plateau) {
        List<Point> mouvementsValides = new ArrayList<Point>(); 

        for (int y = 0; y < plateau.length; y++) {
            for (int x = 0; x < plateau[y].length; x++) {
                Point destination = new Point(x, y);
                // mouvementValide => ajouter à la liste
                if (mouvementValide(position, destination, plateau)) {
                    mouvementsValides.add(destination);
                }
            }
        }
        return mouvementsValides;
    }





}
