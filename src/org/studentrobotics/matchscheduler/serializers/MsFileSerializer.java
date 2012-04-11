package org.studentrobotics.matchscheduler.serializers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.studentrobotics.matchscheduler.Match;
import org.studentrobotics.matchscheduler.Team;

public class MsFileSerializer implements MatchSerializer {

	@Override
	public void serialize(List<Match> matches, List<Team> teams) {
		try {

			BufferedOutputStream bufferedOutput = new BufferedOutputStream(
					new FileOutputStream("matches.ms"));
			PrintStream ps = new PrintStream(bufferedOutput);

			
			for (Match m : matches) {
				for (int i = 0; i < m.getNumberOfTeams(); i++) {
					ps.print(m.getTeams().get(i));
					if (i != m.getNumberOfTeams() - 1)
						ps.print(",");

				}

				ps.print("\n");
			}
			ps.flush();
			ps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Match> parse(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		byte[] fileBytes = new byte[(int) f.length()];
		FileReader fr = new FileReader(f);
		List<Match> result = new ArrayList<Match>();
		for (int i = 0; i < fileBytes.length; i++) {
			try {
				fileBytes[i] = (byte) fr.read();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}

		String s = new String(fileBytes);
		String[] matchSpecs = s.split("\n");
		int i = 0;
		for (String line : matchSpecs) {
			result.add(Match.parse(line, i++));
		}

		return result;
	}

}
