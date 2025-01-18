package com.example.quizapp;

public class QuizQuestions {

    public static String[] quizQuestions = {
            "How much is the area of Lebanon?",
            "Which one is the biggest country in the world?",
            "What is the area of circle?",
    };

    public static String[][] quizAnswers = {
            {"10,452 KM^2", "10, 425 KM^2", "8,452 KM^2", "None of above"},
            {"Canada", "United States America", "Russia", "China"},
            {"2 * pi^2", "2 * pi", "pi * r^2", "pi * r"},
    };

    public static String[][] quizCorrect = {
            {"10,452 KM^2", "25"},
            {"Russia", "10"},
            {"pi * r^2", "25"},
    };

    public static String[] quizSize = {"Small", "Medium", "Large"};
}
