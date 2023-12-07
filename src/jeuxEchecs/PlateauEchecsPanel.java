package jeuxEchecs;

import javax.swing.*;
import java.awt.Point;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Timer;

public class PlateauEchecsPanel extends JPanel {
    private final int lignes = 8;
    private final int colonnes = 8;
    private final int tailleCase = 50;
    private Piece[][] plateau = new Piece[lignes][colonnes];
    private boolean tourBlanc = true; 
    private List<Point> mouvementsValides = new ArrayList<>();
    private JCheckBox iaNoirCheckBox;
    private Timer timerIA;
    private IA iaEchecs;



    private Point pieceSelectionnee = null; 

    public PlateauEchecsPanel(JCheckBox iaNoirCheckBox, IA ia) {
    	this.iaNoirCheckBox = iaNoirCheckBox;
    	this.iaEchecs = ia;
    	
        setPreferredSize(new Dimension(colonnes * tailleCase, lignes * tailleCase));
        initialiserPlateau();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gererClicSouris(e);
            }
        });
        
        timerIA = new Timer(1000, e -> {
            try {
                if (iaNoirCheckBox.isSelected() && !tourBlanc) {
                    jouerCoupIA();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

      
        timerIA.start();
    }

    private void initialiserPlateau() {
    	for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                plateau[i][j] = null;
            }
        }
        //Tours
        plateau[0][0] = new Piece(new ImageIcon("images/BlackRook.png"), TypePiece.TOUR, false);
        plateau[0][7] = new Piece(new ImageIcon("images/BlackRook.png"), TypePiece.TOUR, false);
        plateau[7][0] = new Piece(new ImageIcon("images/WhiteRook.png"), TypePiece.TOUR, true);
        plateau[7][7] = new Piece(new ImageIcon("images/WhiteRook.png"), TypePiece.TOUR, true);

        // Cavaliers
        plateau[0][1] = new Piece(new ImageIcon("images/BlackKnight.png"), TypePiece.CAVALIER, false);
        plateau[0][6] = new Piece(new ImageIcon("images/BlackKnight.png"), TypePiece.CAVALIER, false);
        plateau[7][1] = new Piece(new ImageIcon("images/WhiteKnight.png"), TypePiece.CAVALIER, true);
        plateau[7][6] = new Piece(new ImageIcon("images/WhiteKnight.png"), TypePiece.CAVALIER, true);

        //Fous
        plateau[0][2] = new Piece(new ImageIcon("images/BlackBishop.png"), TypePiece.FOU, false);
        plateau[0][5] = new Piece(new ImageIcon("images/BlackBishop.png"), TypePiece.FOU, false);
        plateau[7][2] = new Piece(new ImageIcon("images/WhiteBishop.png"), TypePiece.FOU, true);
        plateau[7][5] = new Piece(new ImageIcon("images/WhiteBishop.png"), TypePiece.FOU, true);

        //Reines
        plateau[0][3] = new Piece(new ImageIcon("images/BlackQueen.png"), TypePiece.REINE, false);
        plateau[7][3] = new Piece(new ImageIcon("images/WhiteQueen.png"), TypePiece.REINE, true);

        //Rois
        plateau[0][4] = new Piece(new ImageIcon("images/BlackKing.png"), TypePiece.ROI, false);
        plateau[7][4] = new Piece(new ImageIcon("images/WhitekKng.png"), TypePiece.ROI, true);

        //Pions noirs
        for (int i = 0; i < 8; i++) {
            plateau[1][i] = new Piece(new ImageIcon("images/BlackPawn.png"), TypePiece.PION, false);
        }

        //Pions blancs
        for (int i = 0; i < 8; i++) {
            plateau[6][i] = new Piece(new ImageIcon("images/WhitePawn.png"), TypePiece.PION, true);
        }
    }
    
    public Piece[][] getPlateau() {
        return plateau;
    }

    private void gererClicSouris(MouseEvent e) {
        int colonne = e.getX() / tailleCase;
        int ligne = e.getY() / tailleCase;


        //logique pour le joueur humain
        if (pieceSelectionnee == null && plateau[ligne][colonne] != null) {
            if ((plateau[ligne][colonne].estBlanc() && tourBlanc) || (!plateau[ligne][colonne].estBlanc() && !tourBlanc)) {
                pieceSelectionnee = new Point(colonne, ligne);
                calculerMouvementsValides(pieceSelectionnee);
                repaint();
            }
        } else if (pieceSelectionnee != null) {
            if (plateau[pieceSelectionnee.y][pieceSelectionnee.x].mouvementValide(pieceSelectionnee, new Point(colonne, ligne), plateau)) {
                // Vérifiez si la pièce à la destination est un roi avant de faire le mouvement
                if (plateau[ligne][colonne] != null && plateau[ligne][colonne].getType() == TypePiece.ROI) {
                    finDePartie();
                    return;
                }
                //faire le mouvement
                plateau[ligne][colonne] = plateau[pieceSelectionnee.y][pieceSelectionnee.x];
                plateau[pieceSelectionnee.y][pieceSelectionnee.x] = null;
                tourBlanc = !tourBlanc;
                pieceSelectionnee = null;
                mouvementsValides.clear();
                repaint();
            } else {
                pieceSelectionnee = null;
            }
        }
     

    }



    private void finDePartie() {
        String message = "Échec et mat. Voulez-vous jouer une autre partie ?";
        int reponse = JOptionPane.showConfirmDialog(null, message, "Fin de partie", JOptionPane.YES_NO_OPTION);
        if (reponse == JOptionPane.YES_OPTION) {
        	mouvementsValides.clear();
            initialiserPlateau();
            tourBlanc = true;
            pieceSelectionnee = null;
            repaint();
        } else {
        	System.exit(0);
        }
    }

    private void calculerMouvementsValides(Point piece) {
        mouvementsValides.clear();
        Piece pieceActuelle = plateau[piece.y][piece.x];
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                Point destination = new Point(j, i);
                if (pieceActuelle.mouvementValide(piece, destination, plateau)) {
                    mouvementsValides.add(new Point(j, i));
                }
            }
        }
    }
    
    private void verifierEtatJeu() {  
     if (estEnEchecEtMat(tourBlanc)) {finDePartie(); } 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                // Alternance des couleurs de cases
                if ((ligne + colonne) % 2 == 0) {
                    g.setColor(new Color(245, 222, 179)); 
                } else {
                    g.setColor(new Color(139, 69, 19)); 
                }
                g.fillRect(colonne * tailleCase, ligne * tailleCase, tailleCase, tailleCase);
            }
        }

        // Dessiner les mouvements valides
        g.setColor(new Color(255, 200, 0, 128)); 
        for (Point move : mouvementsValides) {
            g.fillRect(move.x * tailleCase, move.y * tailleCase, tailleCase, tailleCase);
        }

        // Dessiner les pièces
        for (int ligne = 0; ligne < lignes; ligne++) {
            for (int colonne = 0; colonne < colonnes; colonne++) {
                if (plateau[ligne][colonne] != null) {
                    g.drawImage(plateau[ligne][colonne].getImageIcon().getImage(), 
                                colonne * tailleCase, ligne * tailleCase, 
                                tailleCase, tailleCase, this);
                }
            }
        }
    }

    private Point localiserRoi(boolean couleurBlanche) {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                Piece piece = plateau[i][j];
                if (piece != null && piece.estBlanc() == couleurBlanche && piece.getType() == TypePiece.ROI) {
                    return new Point(j, i);
                }
            }
        }
        return null; 
    }
    
    private boolean estEnEchecEtMat(boolean couleurBlanche) {
        if (!estEnEchec(couleurBlanche)) {
            return false;
        }
       
        // Vérifiez tous les mouvements possibles pour chaque pièce de la couleur actuelle
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                Piece piece = plateau[i][j];
                if (piece != null && piece.estBlanc() == couleurBlanche) {
                    for (int k = 0; k < lignes; k++) {
                        for (int l = 0; l < colonnes; l++) {
                            if (piece.mouvementValide(new Point(j, i), new Point(l, k), plateau)) {
                                // Effectuer le mouvement 
                                Piece pieceDestination = plateau[k][l];
                                plateau[k][l] = piece;
                                plateau[i][j] = null;
                               
                                boolean echecTemporaire = estEnEchec(couleurBlanche);
                                // Annuler le mouvement
                                plateau[i][j] = piece;
                                plateau[k][l] = pieceDestination;
                                if (!echecTemporaire) {
                                	return false;
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean estEnEchec(boolean couleurBlanche) {
        Point positionRoi = localiserRoi(couleurBlanche);
        
        if (positionRoi == null) {
        	finDePartie();
            //System.err.println("Erreur : Le roi n'a pas été trouvé sur le plateau.");
            return false; 
        }
       
        // Parcourir toutes les pièces du plateau pour voir si l'une peut atteindre le roi
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                Piece piece = plateau[i][j];
                if (piece != null && piece.estBlanc() != couleurBlanche) {
                    if (piece.mouvementValide(new Point(j, i), positionRoi, plateau)) {
                        System.out.println("Échec détecté par la pièce en position: " + i + "," + j);
                        return true; 
                    }
                }
            }
        }
        return false;
    }

    /*private Point choisirPieceNoireAleatoire() {
        List<Point> piecesNoires = new ArrayList<>();
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (plateau[i][j] != null && !plateau[i][j].estBlanc()) {
                    Point position = new Point(j, i);
                    calculerMouvementsValides(position);
                    if (!mouvementsValides.isEmpty()) {
                        piecesNoires.add(position);
                    }
                }
            }
        }
        if (piecesNoires.isEmpty()) {
            return null;
        }
        return piecesNoires.get((int)(Math.random() * piecesNoires.size()));
    }*/

    private void jouerCoupIA() {
        if (!tourBlanc && iaNoirCheckBox.isSelected()) {
            Point meilleurCoup = iaEchecs.choisirMeilleurCoup(plateau, false);
            if (meilleurCoup != null) {
                // Supposons que choisirMeilleurCoup renvoie le point d'arrivée du coup
                Point depart = trouverPieceDeDepart(meilleurCoup);
                if (depart != null) {
                    effectuerCoup(depart, meilleurCoup);
                    tourBlanc = true; 
                    verifierEtatJeu();
                }
            }
        }
    }

    public boolean estTourBlanc() {
        return tourBlanc;
    }
    
    private void effectuerCoup(Point depart, Point arrivee) {
        if (plateau[depart.y][depart.x] != null) {
            
            plateau[arrivee.y][arrivee.x] = plateau[depart.y][depart.x];
            plateau[depart.y][depart.x] = null;
            
            tourBlanc = !tourBlanc;
            verifierEtatJeu();
            repaint();
        }
    }
    
    
    private Point trouverPieceDeDepart(Point arrivee) {
        for (int y = 0; y < lignes; y++) {
            for (int x = 0; x < colonnes; x++) {
                Piece piece = plateau[y][x];
                if (piece != null && (tourBlanc == piece.estBlanc())) {
                    Point depart = new Point(x, y);
                    if (piece.mouvementValide(depart, arrivee, plateau)) {
                        return depart;
                    }
                }
            }
        }
        return null;
    }

    public Point trouverPointDeDepart(Piece[][] plateau, Point arrivee, boolean estBlanc) {
        for (int y = 0; y < plateau.length; y++) {
            for (int x = 0; x < plateau[y].length; x++) {
                Piece piece = plateau[y][x];
                if (piece != null && piece.estBlanc() == estBlanc) {
                    Point depart = new Point(x, y);
                    if (piece.mouvementValide(depart, arrivee, plateau)) {
                        return depart;
                    }
                }
            }
        }
        return null; 
    }
 
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jeu d'échecs");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JCheckBox iaNoirCheckBox = new JCheckBox("IA Noir");
        IA iaEchecs = new IA(2);
        
        PlateauEchecsPanel plateauPanel = new PlateauEchecsPanel(iaNoirCheckBox , iaEchecs);

        // CheckBoxes
        JPanel panelCheckBoxes = new JPanel();
        panelCheckBoxes.setLayout(new BoxLayout(panelCheckBoxes, BoxLayout.Y_AXIS));
        panelCheckBoxes.setBackground(new Color(210, 180, 140)); 
        
        iaNoirCheckBox.addActionListener(e -> {
            if (iaNoirCheckBox.isSelected() && !plateauPanel.estTourBlanc()) {
                Piece[][] etatActuelDuPlateau = plateauPanel.getPlateau();
                Point meilleurCoupIA = iaEchecs.choisirMeilleurCoup(etatActuelDuPlateau, false);
                if (meilleurCoupIA != null) {
                    Point pointDepart = plateauPanel.trouverPointDeDepart(etatActuelDuPlateau, meilleurCoupIA, false);
                    if (pointDepart != null) {
                        plateauPanel.effectuerCoup(pointDepart, meilleurCoupIA);
                        //plateauPanel.verifierEtatJeu();
                        plateauPanel.repaint();
                    }
                }
            }
        });

        // Ajout des cases à cocher au panneau
        panelCheckBoxes.add(iaNoirCheckBox); 
        panelCheckBoxes.add(new JSeparator()); 

        frame.setLayout(new BorderLayout());
        frame.add(panelCheckBoxes, BorderLayout.WEST);
        frame.add(plateauPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
 }