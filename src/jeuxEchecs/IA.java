package jeuxEchecs;

import java.awt.Point;

public class IA {
    private int profondeur;

    public IA(int profondeur) {
        this.profondeur = profondeur;
    }
    
    private int minMax(Piece[][] plateau, int profondeurActuelle, boolean estBlanc) {
        if (profondeurActuelle == this.profondeur) {
            return evaluerPlateau(plateau, estBlanc);
        }

        int meilleurScore = estBlanc ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int y = 0; y < plateau.length; y++) {
            for (int x = 0; x < plateau[y].length; x++) {
                Piece piece = plateau[y][x];
                if (piece != null && piece.estBlanc() == estBlanc) {
                    for (Point mouvement : piece.getMouvementsValides(new Point(x, y), plateau)) {
                        Piece pieceTemp = plateau[mouvement.y][mouvement.x];
                        plateau[mouvement.y][mouvement.x] = piece;
                        plateau[y][x] = null;

                        int score = minMax(plateau, profondeurActuelle + 1, !estBlanc);

                        plateau[y][x] = piece;
                        plateau[mouvement.y][mouvement.x] = pieceTemp;

                        if (estBlanc) {
                            meilleurScore = Math.max(meilleurScore, score);
                        } else {
                            meilleurScore = Math.min(meilleurScore, score);
                        }
                    }
                }
            }
        }
        return meilleurScore;
    }
    
    private static int evaluerPlateau(Piece[][] plateau, boolean estBlanc) {
        int score = 0;

        // Parcourir le plateau et évaluer les pièces
        for (int y = 0; y < plateau.length; y++) {
            for (int x = 0; x < plateau[y].length; x++) {
                Piece piece = plateau[y][x];
                if (piece != null) {
                    int valeurPiece = valeurPiece(piece);
                    score += piece.estBlanc() ? valeurPiece : -valeurPiece;

                   
                    if (piece.getType() == TypePiece.ROI) {
                        score += evaluerSecuriteRoi(x, y, plateau, piece.estBlanc()) * (piece.estBlanc() == estBlanc ? 1 : -1);
                    }
                }
            }
        }
        return score;
    }
    
    public static int valeurPiece(Piece piece) {
        switch (piece.getType()) {
            case ROI: return 900;
            case REINE: return 90;
            case TOUR: return 50;
            case FOU: return 30;
            case CAVALIER: return 30;
            case PION: return 10;
            default: return 0;
        }
    }
    
    public Point choisirMeilleurCoup(Piece[][] plateau, boolean estBlanc) {
        Point meilleurCoup = null;
        int meilleurScore = estBlanc ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int y = 0; y < plateau.length; y++) {
            for (int x = 0; x < plateau[y].length; x++) {
                Piece piece = plateau[y][x];
                if (piece != null && piece.estBlanc() == estBlanc) {
                    for (Point mouvement : piece.getMouvementsValides(new Point(x, y), plateau)) {
                        // Simuler le mouvement
                        Piece pieceTemp = plateau[mouvement.y][mouvement.x];
                        plateau[mouvement.y][mouvement.x] = piece;
                        plateau[y][x] = null;

                        // Évaluer le plateau après le mouvement
                        int score = evaluerPlateau(plateau, estBlanc);
                        // Évaluer la sécurité du roi
                        score += evaluerSecuriteRoi(mouvement.x, mouvement.y, plateau, estBlanc);

                        // Annuler le mouvement
                        plateau[y][x] = piece;
                        plateau[mouvement.y][mouvement.x] = pieceTemp;

                        // Mettre à jour le meilleur coup
                        if ((estBlanc && score > meilleurScore) || (!estBlanc && score < meilleurScore)) {
                            meilleurScore = score;
                            meilleurCoup = new Point(mouvement.x, mouvement.y); 
                        }
                    }
                }
            }
        }
        return meilleurCoup;
    }

    
    private static int evaluerSecuriteRoi(int xRoi, int yRoi, Piece[][] plateau, boolean estBlancRoi) {
        int scoreSecurite = 0;

       
        for (int y = -1; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                if (y == 0 && x == 0) continue; 

                int newY = yRoi + y;
                int newX = xRoi + x;

                //  coordonnées dans les limites du plateau
                if (newY >= 0 && newY < plateau.length && newX >= 0 && newX < plateau[0].length) {
                    Piece piece = plateau[newY][newX];

                    // Si une pièce ennemie est à proximité, diminuer le score de sécurité
                    if (piece != null && piece.estBlanc() != estBlancRoi) {
                        scoreSecurite -= 10; 
                    }
                }
            }
        }

        return scoreSecurite;
    }


}
