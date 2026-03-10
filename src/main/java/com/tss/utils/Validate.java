package com.tss.utils;

import java.util.regex.Pattern;

import static com.tss.utils.GlobalConstants.scanner;

public class Validate {

    public static double validatePercentage() {
        double percentage;
        while (true) {
            System.out.print("Enter percentage (0-100): ");
            if (scanner.hasNextDouble()) {
                percentage = scanner.nextDouble();
                if (percentage >= 0 && percentage <= 100) {
                    scanner.nextLine();
                    return percentage;
                } else {
                    System.out.println("Percentage must be between 0 and 100. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next();
            }
        }
    }

    public static String validateEmail() {

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        String email;

        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();

            if (pattern.matcher(email).matches()) {
                break;
            } else {
                System.out.println("Invalid email. Please try again.");
            }
        }
        return email;
    }

    public static String validatePhoneNumber() {

        String phoneRegex = "^[6-9][0-9]{9}$";
        Pattern pattern = Pattern.compile(phoneRegex);

        String phone;

        while (true) {
            System.out.print("Enter your phone number: ");
            phone = scanner.nextLine();

            if (pattern.matcher(phone).matches()) {
                break;
            } else {
                System.out.println("Invalid phone number. Please enter exactly 10 digits and start with [6-9]");
            }
        }

        return phone;
    }


    public static int validateInt(){
        int temp;
        while(true){
            if(scanner.hasNextInt()){
                temp = scanner.nextInt();
                while(temp<0){
                    System.out.print("Enter positive number: ");
                    temp = validateInt();

                }
                scanner.nextLine();
                return temp;
            }
            else{
                System.out.print("Enter valid number: ");
                scanner.next();
            }
        }
    }
    public static double validateDouble(){
        double temp;
        while(true){
            if(scanner.hasNextDouble()){
                temp = scanner.nextDouble();
                while(temp<0){
                    System.out.print("Enter positive number: ");
                    temp = validateDouble();
                }
                scanner.nextLine();
                return temp;
            }
            else{
                System.out.print("Enter valid number: ");
                scanner.next();
            }
        }
    }
    public static long validateLong(){
        long temp;
        while(true){
            if(scanner.hasNextLong()){
                temp = scanner.nextLong();
                while(temp<0){
                    System.out.print("Enter positive number: ");
                    temp = validateLong();
                }
                scanner.nextLine();
                return temp;
            }
            else{
                System.out.print("Enter valid number: ");
                scanner.next();
            }
        }
    }

    public static String validateString(){
        String temp="";
        while(true){
            temp=scanner.nextLine();
            if(onlyCharInString(temp)){
                return temp;
            }
            System.out.print("Enter only character string: ");
        }
    }

    private static boolean onlyCharInString(String temp) {
        for(int i=0;i<temp.length();i++){
            if(temp.charAt(i)==' ')continue;
            if((temp.charAt(i)<'a' || temp.charAt(i)>'z') && (temp.charAt(i)<'A' || temp.charAt(i)>'Z')){
                return false;
            }
        }
        return true;
    }
}
