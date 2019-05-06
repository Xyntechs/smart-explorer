import java.awt.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.concurrent.CountDownLatch;
public class NewJFrame1 extends javax.swing.JFrame {
    private int posX;
    private int posY;
    private int Ori; //Orientation 0 = right, 1= down, 2=left, 3=up
    private int seenVal;
    private  int pervRead;
    static class Robot {
        private int robotup;
        private int robotdown;
        private int robotright;
        private int robotleft;
        private int sensorup;
        private int sensordown;
        private int ultrasonic;
        private int stop;


        public int getstop() {
            return stop;
        }
        public int getultrasonic() {
            return ultrasonic;
        }
        public int getrobotup() {
            return robotup;
        }
        public int getrobotdown() {
            return robotdown;
        }

        public int getrobotright() {
            return robotright;
        }

        public int getrobotleft() {
            return robotleft;
        }

        public int getsensorup() {
            return sensorup;
        }

        public int getsensordown() {
            return sensordown;
        }



        public void setstop(int stop) {
            this.stop = stop;
        }
        public void setultrasonic(int ultrasonic) {
            this.ultrasonic = ultrasonic;
        }
        public void setrobotup(int robotup) {
            this.robotup = robotup;
        }
        public void setrobotdown(int robotdown) {
            this.robotdown = robotdown;
        }
        public void setrobotright(int robotright) {
            this.robotright = robotright;
        }
        public void setrobotleft(int robotleft) {
            this.robotleft = robotleft;
        }
        public void setsensorup(int sensorup) {
            this.sensorup = sensorup;
        }
        public void setsensordown(int sensordown) {
            this.sensordown = sensordown;
        }




    }
    /**
     * Creates new form NewJFrame1
     */ Robot Rob = new Robot();
     Robot NewRob=new Robot();

    public void inializeRobot()
    {
        Rob.setsensorup(0);
        Rob.setsensordown(0);
        Rob.setrobotup(0);
        Rob.setrobotdown(0);
        Rob.setrobotleft(0);
        Rob.setrobotright(0);
        Rob.setultrasonic(NewRob.getultrasonic());
        Rob.setstop(0);

    }
    private FirebaseDatabase firebaseDatabase;
    /* Get database root reference */
   private DatabaseReference databaseReference;

    /* Get existing child or will be created new child. */
   private DatabaseReference childReference;

    /**
     * initialize firebase.
     */
    private void initFirebase() {
        FirebaseOptions options;//yastaaaaaaaa mtnashah tezbot dh 3al json 3ndk ely fel folder
        try (FileInputStream refreshToken = new FileInputStream("E:/smart-explorer-nodemcu-firebase-adminsdk-mn2gr-a63e36c86d.json")) {

            try {
                options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(refreshToken))
                        .setDatabaseUrl("https://smart-explorer-nodemcu.firebaseio.com/")
                        .build();
                FirebaseApp.initializeApp(options);

                firebaseDatabase = FirebaseDatabase.getInstance();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        databaseReference = firebaseDatabase.getReference("/");
        childReference = databaseReference.child("item");
        childReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                NewRob=snapshot.getValue(Robot.class);
                txtReading.setText(String.valueOf(NewRob.getultrasonic()));
                System.out.println("New Ultrasonic Reading="+NewRob.getultrasonic());
                if(NewRob.getultrasonic()<=50)
                {
                    Mapping();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void save(Robot item) {
        if (item != null) {


            /**
             * The Firebase Java client uses daemon threads, meaning it will not prevent a process from exiting.
             * So we'll wait(countDownLatch.await()) until firebase saves record. Then decrement `countDownLatch` value
             * using `countDownLatch.countDown()` and application will continues its execution.
             */

            CountDownLatch countDownLatch = new CountDownLatch(1);
            childReference.setValue(item, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {

                    System.out.println("Record saved!");
                    // decrement countDownLatch value and application will be continues its execution.
                    countDownLatch.countDown();
                }
            });
            try {
                //wait for firebase to saves record.
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void Mapping()
    {
        int CurrentReading;
        CurrentReading= NewRob.getultrasonic();
        if(CurrentReading==pervRead) return;
        MapDrawer.getGraphics().clearRect(posX*10+30,posY*10+30, 10,10);
        MapDrawer.getGraphics().setColor(Color.GREEN);
        if(NewRob.getstop()==1) {
            MapDrawer.getGraphics().setColor(Color.BLACK);
            seenVal = CurrentReading;
            if (Ori == 0) {
                MapDrawer.getGraphics().drawLine(posX * 10 + CurrentReading * 10 + 10, posY * 10 + 10, posX * 10 + CurrentReading * 10 + 10, posY * 10 + 50);
            } else if (Ori == 1) {
                MapDrawer.getGraphics().drawLine(posX * 10 + 10, posY * 10 + CurrentReading * 10 + 10, posX * 10 + 50, posY * 10 + CurrentReading * 10 + 10);
            } else if (Ori == 2) {
                MapDrawer.getGraphics().drawLine(posX * 10 - CurrentReading * 10 + 10, posY * 10 + 10, posX* 10 - CurrentReading * 10 + 10, posY * 10 + 50);
            } else {
                MapDrawer.getGraphics().drawLine(posX * 10 + 10, posY * 10 - CurrentReading * 10 + 10, posX * 10 + 50, posY * 10 - CurrentReading * 10 + 10);
            }
        }
        else
        {
            if(Ori==0) {
                posX=posX+seenVal-CurrentReading;
            }
            else if(Ori==1)
            {
                posY=posY+seenVal-CurrentReading;
            }
            else if(Ori==2)
            {
                posX=posX-(seenVal-CurrentReading);

            }
            else
            {
                posY = posY-(seenVal-CurrentReading);
            }
            seenVal = CurrentReading;
        }
        MapDrawer.getGraphics().fillRect(posX*10+30,posY*10+30,10,10);
        MapDrawer.getGraphics().drawRect(0,0, 799,399);
        System.out.println(posX);
        System.out.println(posY);
        System.out.println(Ori);
        System.out.println(seenVal);
        pervRead = CurrentReading;
    }
    public NewJFrame1() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        btnRobotDown = new java.awt.Button();
        btnSensorUp = new java.awt.Button();
        btnRobotStop = new java.awt.Button();
        lblReading = new java.awt.Label();
        txtReading = new java.awt.TextField();
        lblSensor = new java.awt.Label();
        lblRobot = new java.awt.Label();
        btnRobotRight = new java.awt.Button();
        btnRobotLeft = new java.awt.Button();
        btnRobotUp = new java.awt.Button();
        btnSensorDown = new java.awt.Button();
        lblMap = new java.awt.Label();
        MapDrawer = new java.awt.Canvas();
        lblTitle = new java.awt.Label();
        posX=0;
        posY=0;
        Ori=0; //Orientation 0 = right, 1= down, 2=left, 3=up
        seenVal=0;
        pervRead =0;

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
                jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
                jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Smart Explorer");
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        btnRobotDown.setBackground(new java.awt.Color(51, 204, 0));
        btnRobotDown.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRobotDown.setLabel("Move Backward");
        btnRobotDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRobotDownActionPerformed(evt);
            }
        });

        btnSensorUp.setBackground(new java.awt.Color(51, 204, 0));
        btnSensorUp.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnSensorUp.setLabel("Move Forward");
        btnSensorUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSensorUpActionPerformed(evt);
            }
        });

        btnRobotStop.setBackground(new java.awt.Color(255, 0, 0));
        btnRobotStop.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRobotStop.setLabel("Stop!");
        btnRobotStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRobotStopActionPerformed(evt);
            }
        });

        lblReading.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblReading.setText("Ultrasonic Reading");

        txtReading.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        txtReading.setText("0");

        lblSensor.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblSensor.setText("Servo Orientation");

        lblRobot.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblRobot.setText("Robot Controller");

        btnRobotRight.setBackground(new java.awt.Color(51, 204, 0));
        btnRobotRight.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRobotRight.setLabel("Turn Right");
        btnRobotRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRobotRightActionPerformed(evt);
            }
        });

        btnRobotLeft.setBackground(new java.awt.Color(51, 204, 0));
        btnRobotLeft.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRobotLeft.setLabel("Turn Left");
        btnRobotLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRobotLeftActionPerformed(evt);
            }
        });

        btnRobotUp.setBackground(new java.awt.Color(51, 204, 0));
        btnRobotUp.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnRobotUp.setLabel("Move Forward");
        btnRobotUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRobotUpActionPerformed(evt);
            }
        });

        btnSensorDown.setBackground(new java.awt.Color(51, 204, 0));
        btnSensorDown.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnSensorDown.setLabel("Move Backward");
        btnSensorDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSensorDownActionPerformed(evt);
            }
        });

        lblMap.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblMap.setText("Map");

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTitle.setText("Smart Explorer");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(132, 132, 132)
                                                .addComponent(txtReading, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(87, 87, 87)
                                                .addComponent(lblReading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnSensorUp, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnSensorDown, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(115, 115, 115))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(lblSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(131, 131, 131))))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(480, 480, 480)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(btnRobotUp, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(btnRobotLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(24, 24, 24)
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(btnRobotStop, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnRobotDown, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addGap(22, 22, 22)
                                                .addComponent(btnRobotRight, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(650, 650, 650)
                                                .addComponent(lblMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(600, 600, 600)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(lblRobot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(280, 280, 280)
                                                .addComponent(MapDrawer, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(87, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(lblReading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtReading, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(lblRobot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(btnRobotUp, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                                                .addComponent(lblSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        )
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(btnSensorUp, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnSensorDown, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnRobotStop, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnRobotDown, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(btnRobotRight, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnRobotLeft, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)

                                                        )))
                                .addGap(0, 0, 0)
                                .addComponent(lblMap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(MapDrawer, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41))
        );

        lblSensor.getAccessibleContext().setAccessibleName("Servo Orientation");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void btnRobotRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRobotRightActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setrobotright(1);
        if(Ori == 3) {
            Ori = 0;
        }
        if(Ori==1) {
            Ori = 2;
        }
        if(Ori==2) {
            Ori = 3;
        }
        if(Ori==0) {
            Ori = 1;
        }
        save(Rob);
        //while(NewRob.getstop()!=1);
        /*inializeRobot();
        Rob.setrobotup(1);
        save(Rob);*/

    }//GEN-LAST:event_btnRobotRightActionPerformed

    private void btnRobotUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRobotUpActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setrobotup(1);
        save(Rob);
    }//GEN-LAST:event_btnRobotUpActionPerformed

    private void btnRobotStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRobotStopActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setstop(1);
        save(Rob);

    }//GEN-LAST:event_btnRobotStopActionPerformed

    private void btnRobotLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRobotLeftActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setrobotleft(1);
        save(Rob);
        if(Ori == 0) {
            Ori = 3;
        }
        if(Ori==3) {
            Ori = 2;
        }
        if(Ori==2) {
            Ori = 1;
        }
        if(Ori==1) {
            Ori = 0;
        }
        save(Rob);
        //while(NewRob.getstop()!=1);
        /*
        inializeRobot();
        Rob.setrobotup(1);
        save(Rob);*/
    }//GEN-LAST:event_btnRobotLeftActionPerformed

    private void btnRobotDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRobotDownActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setrobotdown(1);
        save(Rob);
        if(Ori == 0) {
            Ori = 2;
        }
        if(Ori==1) {
            Ori = 3;
        }
        if(Ori==2) {
            Ori = 0;
        }
        if(Ori==3) {
            Ori = 1;
        }
        save(Rob);
    }//GEN-LAST:event_btnRobotDownActionPerformed

    private void btnSensorUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSensorUpActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setsensorup(1);
        save(Rob);
    }//GEN-LAST:event_btnSensorUpActionPerformed


    private void btnSensorDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSensorDownActionPerformed
        // TODO add your handling code here:
        inializeRobot();
        Rob.setsensordown(1);
        save(Rob);
    }//GEN-LAST:event_btnSensorDownActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewJFrame1 frame= new NewJFrame1();


                frame.setVisible(true);
                frame.initFirebase();

            }
        });
    }


    // Variables declaration - do not modify
    private java.awt.Canvas MapDrawer;
    private java.awt.Button btnRobotDown;
    private java.awt.Button btnRobotLeft;
    private java.awt.Button btnRobotRight;
    private java.awt.Button btnRobotStop;
    private java.awt.Button btnRobotUp;
    private java.awt.Button btnSensorDown;
    private java.awt.Button btnSensorUp;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JPanel jPanel1;
    private java.awt.Label lblMap;
    private java.awt.Label lblReading;
    private java.awt.Label lblRobot;
    private java.awt.Label lblSensor;
    private java.awt.Label lblTitle;
    private java.awt.TextField txtReading;
    // End of variables declaration
}
