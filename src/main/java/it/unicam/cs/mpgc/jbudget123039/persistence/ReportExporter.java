package it.unicam.cs.mpgc.jbudget123039.persistence;

import java.io.File;
import java.io.IOException;

public interface ReportExporter {
    void exportReport(File destination) throws IOException;
}