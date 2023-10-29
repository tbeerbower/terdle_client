package com.techelevator.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.stream.IntStream.range;
import static java.util.stream.Stream.of;

public class TextGrid {

    public enum HorizontalAlign {
        LEFT, CENTER, RIGHT
    }

    public enum VerticalAlign {
        TOP, CENTER, BOTTOM
    }

    private static final int MIN_CELL_WIDTH = 1;
    private static final int MIN_CELL_HEIGHT = 1;
    private static final int DEFAULT_MAX_CELL_WIDTH = 40;
    private static final int DEFAULT_MAX_CELL_HEIGHT = 12;

    private final List<String[]> displayLines;

    private TextGrid(List<String[]> displayLines) {
        this.displayLines = displayLines;
    }

    public void show(PrintStream out) {
        for (String[] lines : displayLines) {
            Arrays.stream(lines).forEach(out::println);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String[] lines : displayLines) {
            for (String line : lines) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    // ***** inner class : Builder ********************************************

    public static class Builder {

        private static final CellText[] EMPTY_CELL_TEXTS = {};


        private enum CornerChars {
            BASIC(new char[]{'+', '+', '+', '+', '+', '+', '+', '+', '+', '-', '|'}),
            XASCII_DOUBLE(new char[]{0x256C, 0x2563, 0x2569, 0x255D, 0x2560, 0x255A, 0x2566, 0x2557, 0x2554, 0x2550, 0x2551});
            private final char[] chars;

            CornerChars(char[] chars) {
                this.chars = chars;
            }

            private String get(int index) {
                return String.valueOf(chars[index]);
            }
        }

        private static final int[] CORNER_CODES = {
                0, 1, 0, 0, 2, 3, 2, 0,
                0, 1, 0, 0, 0, 0, 0, 3,
                0, 0, 4, 0, 2, 2, 5, 0,
                0, 0, 4, 0, 0, 0, 0, 5,
                0, 1, 0, 0, 0, 1, 0, 0,
                6, 7, 6, 0, 0, 0, 0, 7,
                0, 0, 4, 0, 0, 0, 4, 0,
                6, 6, 8, 0, 0, 0, 0, 8,
        };

        private final List<Cell> cells = new ArrayList<>();
        private VerticalAlign verticalAlign = VerticalAlign.CENTER;
        private HorizontalAlign horizontalAlign = HorizontalAlign.CENTER;
        private final boolean enableColumnAttributes;
        private final boolean enableRowAttributes;
        private final int[] cellWidths;
        private int[] cellHeights;
        private int maxCellWidth = DEFAULT_MAX_CELL_WIDTH;
        private int maxCellHeight = DEFAULT_MAX_CELL_HEIGHT;
        private final int gridWidth;
        private int gridHeight;
        private int verticalCellPadding = 0;
        private int horizontalCellPadding = 0;
        private boolean hasBorder = true;
        private TextEffect fillEffect = null;
        private CornerChars cornerChars = CornerChars.XASCII_DOUBLE;

        public Builder(int gridWidth) {
            this(gridWidth, true);
        }

        public Builder(int gridWidth, boolean hasBorder) {
            this(gridWidth, false, false, hasBorder);
        }
        public Builder(int gridWidth, boolean enableRowAttributes, boolean enableColumnAttributes, boolean hasBorder) {
            this.gridWidth = gridWidth;
            this.enableRowAttributes = enableRowAttributes;
            this.enableColumnAttributes = enableColumnAttributes;
            this.cellHeights = new int[]{MIN_CELL_HEIGHT};
            this.cellWidths = new int[gridWidth];
            this.hasBorder = hasBorder;
            IntStream.range(0, gridWidth).forEach(i -> this.cellWidths[i] = MIN_CELL_WIDTH);
        }


        public Builder addCell(Cell cell) {
            int row = cells.size() / gridWidth;
            int col = cells.size() % gridWidth;
            adjustCellDimensions(row, col, cell.getTextLines());
            cells.add(cell);
            return this;
        }

        public Builder addCell(CellText... textLines) {
            return addCell(new Cell(textLines));
        }

        public Builder addCell(TextEffect fillEffect, CellText... textLines) {
            return addCell(new Cell(fillEffect, textLines));
        }

        public Builder addCell(String... textLines) {
            return addCell(Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder addCell(TextEffect fillEffect, String... textLines) {
            return addCell(fillEffect, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder setCell(int row, int col, Cell cell) throws IllegalArgumentException {
            if (col >= gridWidth) {
                throw new IllegalArgumentException(String.format("Grid width is %d.  Valid values for col are 0 - %d.", gridWidth, gridWidth - 1));
            }
            adjustCellDimensions(row, col, cell.getTextLines());
            int index = row * gridWidth + col;
            if (index >= cells.size()) {
                IntStream.rangeClosed(cells.size(), index).forEach(i ->
                    cells.add(i, new Cell(new CellText[]{})));
            }
            cells.set(index, cell);
            return this;
        }

        public Builder setCell(int row, int col, CellText... textLines) throws IllegalArgumentException {
            return setCell(row, col, new Cell(textLines));
        }

        public Builder setCell(int row, int col, String... textLines) throws IllegalArgumentException {
            return setCell(row, col, Arrays.stream(textLines).map(CellText::new).toArray(CellText[]::new));
        }

        public Builder setMaxCellWidth(int maxCellWidth) {
            this.maxCellWidth = maxCellWidth;
            return this;
        }

        public Builder setMaxCellHeight(int maxCellHeight) {
            this.maxCellHeight = maxCellHeight;
            return this;
        }

        public Builder setVerticalAlignment(VerticalAlign verticalAlign) {
            this.verticalAlign = verticalAlign;
            return this;
        }

        public Builder setHorizontalAlign(HorizontalAlign horizontalAlign) {
            this.horizontalAlign = horizontalAlign;
            return this;
        }

        public Builder setVerticalCellPadding(int verticalCellPadding) {
            this.verticalCellPadding = verticalCellPadding;
            return this;
        }

        public Builder setHorizontalCellPadding(int horizontalCellPadding) {
            this.horizontalCellPadding = horizontalCellPadding;
            return this;
        }

        public Builder setHasBorder(boolean hasBorder) {
            this.hasBorder = hasBorder;
            return this;
        }

        public Builder setFillEffect(TextEffect fillEffect) {
            this.fillEffect = fillEffect;
            return this;
        }

        public Builder setCornerChars(CornerChars cornerChars) {
            this.cornerChars = cornerChars;
            return this;
        }

        public TextGrid generate() {
            gridHeight = cells.size() / gridWidth + (cells.size() % gridWidth == 0 ? 0 : 1);
            return new TextGrid(range(0, gridHeight).mapToObj(this::generateCellRow).collect(Collectors.toList()));
        }

        private String[] generateCellRow(int row) {
            int height = getPaddedCellHeight(row);
            return range(0, gridWidth).mapToObj(col -> generateCell(row, col,
                            getCellText(row * gridWidth + col), height, getPaddedCellWidth(col),
                            getCellFillEffect(row * gridWidth + col)))
                .reduce((cell1, cell2) -> range(0, hasBorder ? (height + (row == 0 ? 2 : 1)) : height).mapToObj(i ->
                    format("%s%s", cell1[i], cell2[i])).toArray(String[]::new))
                .orElse(new String[0]);
        }

        private String[] generateCell(int row, int col, CellText[] cellText, int height, int width, TextEffect cellFillEffect) {
            boolean isLastCol = col == gridWidth - 1;

            if (hasBorder) {
                boolean isLastRow = isLastRow(row);
                boolean isFirstRow = row == 0;
                boolean ifFirstCol = col == 0;

                String topLeftChar = cornerChar(cellFillEffect, true, true, isFirstRow, isLastRow, ifFirstCol, isLastCol);
                String bottomLeftChar = cornerChar(cellFillEffect, false, true, isFirstRow, isLastRow, ifFirstCol, isLastCol);
                String topRightChar = cornerChar(cellFillEffect, true, false, isFirstRow, isLastRow, ifFirstCol, isLastCol);
                String bottomRightChar = cornerChar(cellFillEffect, false, false, isFirstRow, isLastRow, ifFirstCol, isLastCol);
                String verticalChar = verticalChar(cellFillEffect);
                String horizontalChar = horizontalChar(cellFillEffect);

                String top = format("%s%s%s", topLeftChar, horizontalChar.repeat(width), isLastCol ? topRightChar : "");
                String bottom = format("%s%s%s", bottomLeftChar, horizontalChar.repeat(width), isLastCol ? bottomRightChar : "");
                Stream<String> centerStream = range(0, height).mapToObj(textRow ->
                        format("%s%s%s", verticalChar, getDisplayText(cellText, row, textRow, width, cellFillEffect),
                                isLastCol ? verticalChar : ""));

                return (isFirstRow ?
                        of(of(top), centerStream, of(bottom)) : of(centerStream, of(bottom))).
                        flatMap(Function.identity()).toArray(String[]::new);
            }
            Stream<String> centerStream = range(0, height).mapToObj(textRow ->
                    format("%s", getDisplayText(cellText, row, textRow, width, cellFillEffect)));

            return (of(centerStream)).flatMap(Function.identity()).toArray(String[]::new);
        }

        private String verticalChar(TextEffect cellFillEffect) {
            return applyEffect(cellFillEffect, String.valueOf(cornerChars.get(10)));
        }

        private String horizontalChar(TextEffect cellFillEffect) {
            return applyEffect(cellFillEffect, String.valueOf(cornerChars.get(9)));
        }

        private String cornerChar(TextEffect cellFillEffect, boolean... bits) {
            int key = 0;
            for (boolean bit : bits) {
                key = (key << 1) + (bit ? 1 : 0);
            }
            return applyEffect(cellFillEffect, String.valueOf(cornerChars.get(CORNER_CODES[key])));
        }

        private String applyEffect(TextEffect cellFillEffect, String cc) {
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            return effect == null ? cc : effect.apply(cc);
        }

        private CellText[] getCellText(int index) {
            return index < cells.size() ? cells.get(index).getTextLines() : EMPTY_CELL_TEXTS;
        }

        private TextEffect getCellFillEffect(int index) {
            return index < cells.size() ? cells.get(index).getFillEffect() : null;
        }

        private int getCellHeight(int row) {
            return cellHeights[enableRowAttributes ? row : 0];
        }

        private int getPaddedCellHeight(int row) {
            return getCellHeight(row) + verticalCellPadding * 2;
        }

        private int getPaddedCellWidth(int col) {
            return cellWidths[enableColumnAttributes ? col : 0] + horizontalCellPadding * 2;
        }

        private void adjustCellDimensions(int row, int col, CellText[] textLines) {
            row = enableRowAttributes ? row : 0;
            final int finalCol = enableColumnAttributes ? col : 0;
            Arrays.stream(textLines).forEachOrdered(cellText ->
                cellWidths[finalCol] = Math.min(maxCellWidth, Math.max(cellWidths[finalCol], cellText.getRawText().length())));
            int heights = cellHeights.length;
            if (heights <= row) {
                cellHeights = Arrays.copyOf(cellHeights, row + 1);
                IntStream.rangeClosed(heights, row).forEach(i -> cellHeights[i] = MIN_CELL_HEIGHT);
            }
            cellHeights[row] = Math.min(maxCellHeight, Math.max(cellHeights[row], textLines.length));
        }

        private String getDisplayText(CellText[] cellTexts, int gridRow, int textRow, int width, TextEffect cellFillEffect) {
            if (cellTexts.length < getCellHeight(gridRow) && verticalAlign != VerticalAlign.TOP) {
                textRow -= verticalAlign == VerticalAlign.CENTER ?
                    Math.ceil((getCellHeight(gridRow) - cellTexts.length + verticalCellPadding) / 2.0) :
                    getCellHeight(gridRow) - cellTexts.length + verticalCellPadding;
            } else {
                textRow -= verticalCellPadding;
            }
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            String fill = effect == null ? " ".repeat(width) : effect.apply(" ".repeat(width));

            return (textRow >= 0 && textRow < getCellHeight(gridRow) && textRow < cellTexts.length) ?
                getDisplayText(cellTexts[textRow], width, cellFillEffect) : fill;
        }

        private String getDisplayText(CellText cellText, int width, TextEffect cellFillEffect) {
            int textWidth = width - horizontalCellPadding * 2;
            TextEffect effect = cellFillEffect == null ? fillEffect : cellFillEffect;
            String fill = effect == null ? " ".repeat(horizontalCellPadding) :
                    effect.apply(" ".repeat(horizontalCellPadding));
            return String.format("%s%s%s", fill, cellText.getDisplayText(textWidth, horizontalAlign, horizontalChar(cellFillEffect), cellFillEffect), fill);
        }

        private boolean isLastRow(int row) {
            return row == gridHeight - 1;
        }
    }

    // ***** inner class : Cell ***********************************************

    public static class Cell {
        private final CellText[] textLines;
        private final TextEffect fillEffect;

        public Cell(CellText... textLines) {
            this(null, textLines);
        }
        public Cell(TextEffect fillEffect, CellText... textLines) {
            this.fillEffect = fillEffect;
            this.textLines = textLines;
        }

        public CellText[] getTextLines() {
            return textLines;
        }

        public TextEffect getFillEffect() {
            return fillEffect;
        }
    }
    
    // ***** inner class : CellText *******************************************

    public static class CellText {

        private final String text;
        private final TextEffect effect;

        public CellText(String text) {
            this(text, null);
        }

        public CellText(String text, TextEffect effect) {
            this.text = text;
            this.effect = effect;
        }
        public String getRawText() {
            return text;
        }

        public String getDisplayText(int width, HorizontalAlign horizontalAlign, String horizontalChar, TextEffect cellFillEffect) {
            String displayText = horizontalAlign == HorizontalAlign.CENTER ? centerString(text, width) :
                format(format("%%%s%d.%ds", horizontalAlign == HorizontalAlign.LEFT ? horizontalChar : "", width, width), text);
            TextEffect cellEffect = cellFillEffect == null ? effect : cellFillEffect;

            return cellEffect == null ? displayText : cellEffect.apply(displayText);
        }

        private static String centerString(String s, int width) {
            return width > s.length() ?
                format(format("%%-%ds", width), format(format("%%%ds", s.length() + (width - s.length()) / 2), s)) :
                s.substring(0, width);
        }
    }
}
