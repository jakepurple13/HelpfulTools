package com.programmersbox.testingplaygroundapp

import com.programmersbox.gsonutils.fromJson
import com.programmersbox.helpfulutils.randomRemove

object Names {
    val names: MutableList<String> by lazy {
        """
    ["Aaron Thetires", "Abbie Birthday", "Abe Rudder", "Abel N. Willan", "Adam Baum", "Adam Bomb", "Adam Meway", "Adam Sapple", "Adam Zapel", "Agatha L. Outtathere", "Al B. Tross", "Al B. Zienya", "Al Dente", "Al Fresco", "Al Gore Rythim", "Al K. Seltzer", "Al Kaholic", "Al Kaseltzer", "Al Luminum", "Al Nino", "Al O'Moaney", "Alec Tricity", "Alex Blaine Layder", "Alf A. Romeo", "Alf Abet", "Ali Gaither", "Ali Gator", "Ali Katt", "Allen Rench", "Amanda B. Reckonwith", "Amanda Lynn", "Andy Friese", "Andy Gravity", "Andy Structible", "Anita Bath", "Anita Bathe", "Anita Job", "Anita Knapp", "Ann B. Dextrous", "Ann Chovie", "Ann Tartica", "Anna Conda", "Anna Graham", "Anna Mull", "Anna Prentice", "Anna Sasin", "Anna Septic", "Anne T. Lope", "Anne Teak", "Anne Ville", "Annette Curtain", "Annie Buddyhome", "Annie Howe", "Annie Matter", "Annie Moore", "April Schauer", "Arch N. Emmy", "Aretha Holly", "Ariel Hassle", "Armand Hammer", "Art Exhibit", "Art Major", "Art Painter", "Art Sellers", "Artie Choke", "Ayma Moron", "B. A. Ware", "Barb Dwyer", "Barb Dwyer", "Barb E. Cue", "Barb E. Dahl", "Barry Cade", "Barry D'Alive", "Barry D. Hatchett", "Barry Shmelly", "Bart Ender", "Bea Lowe", "Bea Minor", "Bea Sting", "Beau Tye", "Beau Vine", "Ben Crobbery", "Ben D. Fender", "Ben Dover", "Ben Down", "Ben Lyon", "Ben O'Drill", "Ben Thair", "Bertha D. Blues", "Bess Eaton", "Bess Twishes", "Biff Wellington", "Bill Board", "Bill Ding", "Bill Dollar", "Bill Foldes", "Bill Loney", "Bill Lowney", "Bill Ng", "Bill Overdew", "Billy Rubin", "Bjorn Free", "Bo D. Satva", "Bo Nessround", "Bob Frapples", "Bob Inforapples", "Bob Katz", "Bob Ng", "Bob Sledd", "Bonnie Ann Clyde", "Bowen Arrow", "Brandon Cattell", "Brandon Irons", "Brandy Anne Koch", "Brandy Bottle", "Brandy D. Cantor", "Braxton Hicks", "Brice Tagg", "Brighton Early", "Brock Lee", "Brook Lynn Bridge", "Brooke Trout", "Brooke Waters", "Bruce Easley", "Buck Ng", "Bud Weiser", "Buddy Booth", "Buddy System", "C. Worthy", "Cal Culator", "Cal Efornia", "Cal Seeium", "Cam Payne", "Cammie Sole", "Candace Spencer", "Candice B. DePlace", "Candice B. Fureal", "Candy Barr", "Candy Baskett", "Candy Kane", "Cara Van", "Carl Arm", "Carlotta Tendant", "Carrie A. Tune", "Carrie Dababi", "Carrie Oakey", "Carson O. Gin", "Chad Terbocks", "Chanda Lear", "Charity Case", "Cheri Pitts", "Chi Spurger", "Chip Munk", "Chris Coe", "Chris Cross", "Chris Ko", "Chris Mass", "Chris P. Bacon", "Chris P. Nugget", "Chris P. Wheatzenraisins", "Chrystal Glass", "Chuck Roast", "Claire Annette", "Claire Annette Reed", "Claire DeAir", "Claire Voyance", "Clara Nett", "Clara Sabell", "Cody Pendant", "Cole Durkee", "Cole Kutz", "Colette A. Day", "Colin Allcars", "Colleen Cardd", "Constance Noring", "Corey Ander", "Count Orff", "Crystal Ball", "Crystal Claire Waters", "Crystal Glass", "Curt N. Rodd", "Curt Zee", "Cy Burns", "Cy Kosis", "Daisy Chain", "Daisy Picking", "Dale E. Bread", "Dan D. Lyon", "Dan Deline", "Dan Druff", "Dan Geruss", "Dan Saul Knight", "Danielle Soloud", "Darrell B. Moore", "Darren Deeds", "Darryl Likt", "Dee Kay", "Dee Liver", "Dee Major", "Dee Sember", "Dee Zaster", "Dennis Toffice", "Denny Juan Heredatt", "Des Buratto", "Di O'Bolic", "Diane Toluvia", "Didi Reelydoit", "Dinah Might", "Dinah Soares", "Doll R. Bill", "Don Key", "Don Thatt", "Doris Open", "Doris Schutt", "Doug Graves", "Doug Hole", "Doug Love Fitzhugh", "Doug Updegrave", "Doug Witherspoon", "Douglas Furr", "Douglas S. Halfempty", "Drew Blood", "Duane DeVane", "Duane Pipe", "Dustin D. Furniture", "Dusty Carr", "Dusty Rhodes", "Dusty Sandmann", "Dusty Storm", "Dwayne Pipe", "E. Ville", "Earl E. Byrd", "Earl Lee Riser", "Easton West", "Eaton Wright", "Ed Ible", "Ed Jewcation", "Ed Venture", "Eddie Bull", "Eileen Dover", "Eli Ondefloor", "Ella Vader", "Elle O'Quent", "Ellie Noise", "Elmer Sklue", "Emerald Stone", "Emile Eaton", "Emma Royds", "Estelle Hertz", "Ethel L. Cahall", "Evan Keel", "Evan Lee Arps", "Evans Gayte", "Eve Hill", "Eve Ning", "Eve O'Lution", "Ewan Whatarmy", "Father A. Long", "Faye Kinnitt", "Faye Slift", "Faye Tallity", "Ferris Wheeler", "Fletcher Bisceps", "Ford Parker", "Frank Enstein", "Frank Furter", "Frank N. Beans", "Frank N. Sense", "Frank N. Stein", "Freida Convict", "Gene E. Yuss", "Gene Poole", "George Washington Sleptier", "Gil T. Azell", "Ginger Rayl", "Ginger Snapp", "Ginger Vitis", "Gladys C. Hughes", "Gladys Eeya", "Godiva Headache", "Gus Tofwin", "Hal E. Luya", "Hal Jalikakick", "Hammond Eggs", "Hare Brain", "Harmon Ikka", "Harrison Fire", "Harry Armand Bach", "Harry Beard", "Harry Caray", "Harry Chest", "Harry Legg", "Harry Pitts", "Harry R. M. Pitts", "Hayden Seek", "Haywood Jashootmee", "Hazel Nutt", "Heather N. Yonn", "Hein Noon", "Helen Back", "Helen Highwater", "Helena Hanbaskett", "Herb E. Side", "Herbie Voor", "Hilda Climb", "Holly Day", "Holly Wood", "Homan Provement", "Hope Ferterbest", "Howard I. No", "Howe D. Pardner", "Howie Doohan", "Hugh Mungous", "Hugh deMann", "Hugo First", "Hy Ball", "Hy Gene", "Hy Lowe", "Hy Marx", "Hy Price", "I. Ball", "I. D. Clair", "I. Lasch", "I. M. Boring", "I. P. Daly", "I. P. Freely", "I. Pullem", "I. Ron Stomach", "Ida Whana", "Igor Beaver", "Ilene Dover", "Ilene East", "Ilene Left", "Ilene North", "Ilene South", "Ilene West", "Ilene Wright", "Ima B. Leever", "Ima Hogg", "Ima Klotz", "Ima Lytle Teapot", "Iona Corolla", "Iona Ford", "Iona Frisbee", "Ira Fuse", "Isadore Bell", "Ivan Oder", "Izzy Backyet", "Jack Dupp", "Jack Hammer", "Jack Pott", "Jack Tupp", "Jacklyn Hyde", "Jacques Strap", "Jade Stone", "Jan U. Wharry", "Jane Linkfence", "Jaqueline Hyde", "Jasmine Flowers", "Jasmine Rice", "Jay Bird", "Jay Walker", "Jean Poole", "Jeanette Akenja Nearing", "Jed Dye", "Jed I. Knight", "Jeff Healitt", "Jerry Atrics", "Jim Laucher", "Jim Nasium", "Jim Shorts", "Jim Shu", "Jim Sox", "Jimmy DeLocke", "Jo King", "Joanna Hand", "Joaquin DeFlores", "Joe Czarfunee", "Joe Kerr", "Joe King", "Joy Anna DeLight", "Joy Kil", "Joy Rider", "Juan De Hattatime", "Juan Fortharoad", "Juan Morefore DeRhode", "Juan Nightstand", "Juana Bea", "June Bugg", "Justin Case", "Justin Casey Howells", "Justin Credible", "Justin Inch", "Justin Sane", "Justin Thyme", "Justin Tune", "Kandi Apple", "Kareem O'Weet", "Kat Toy", "Katy Litter", "Kay Mart", "Kay Neine", "Ken Dahl", "Ken Oppenner", "Kenney C. Strait", "Kenny Dewitt", "Kenny Penny", "Kent Cook", "Kenya Dewit", "Kerry Oki", "Kim Payne Slogan", "Kitty Katz", "Kristie Hannity", "Kurt Remarque", "Lake Speed", "Lance Lyde", "Laura Norder", "Lee Nover", "Leigh King", "Len DeHande", "Leo Tarred", "Les Moore", "Les Payne", "Les Plack", "Lily Livard", "Lily Pond", "Lina Ginster", "Lisa Carr", "Lisa Ford", "Lisa Honda", "Lisa Neucar", "Liv Good", "Liv Long", "Liz Onnia", "Lois Price", "Lon Moore", "Lou Briccant", "Lou Dan Obseen", "Lou Pole", "Lou Stooth", "Lou Zar", "Louise E. Anna", "Lowden Clear", "Lucy Fer", "Luke Adam Go", "Luke Warm", "Luna Tick", "Lynn Guini", "Lynn Meabuck", "Lynn O. Liam", "M. Balmer", "M. T. Toombe", "Mabel Syrup", "Macon Paine", "Mandy Lifeboats", "Manny Kinn", "Manuel Labor", "Marco DeStinkshun", "Marcus Absent", "Marge Innastraightline", "Marj Oram", "Mark A. Roni", "Mark Mywords", "Mark Z. Spot", "Marlon Fisher", "Marsha Dimes", "Marsha Mellow", "Marshall Law", "Marty Graw", "Marv Ellis", "Mary A. Richman", "Mary Ann Bright", "Mary Gold", "Mary Ott", "Mary Thonn", "Mason Jarr", "Matt Tress", "Maude L. T. Ford", "Maurice Minor", "Max E. Mumm", "Max Little", "Max Power", "May Day", "May Furst", "May K. Fist", "May O'Nays", "Megan Bacon", "Mel Function", "Mel Loewe", "Mel Practiss", "Melanie Letters", "Melba Crisp", "Michael Otto Nuys", "Michelle Lynn", "Midas Well", "Mike Czech", "Mike Raffone", "Mike Rohsopht", "Mike Stand", "Milly Graham", "Milly Meter", "Milton Yermouth", "Minnie Skurt", "Minny van Gogh", "Miss Alanius", "Missy Sippy", "Misty C. Shore", "Misty Meanor", "Misty Shore", "Misty Waters", "Mitch Again", "Moe DeLawn", "Moe Skeeto", "Molly Kuehl", "Morey Bund", "Morgan U. Canhandle", "Mort Tallity", "Myles Long", "Myra Maines", "Neil B. Formy", "Neil Down", "Neve Adda", "Nick L. Andime", "Nick O'Teen", "Nick O'Time", "Nick Ovtime", "Nida Lyte", "Noah Lott", "Noah Riddle", "Olive Branch", "Olive Green", "Olive Hoyl", "Olive Yew", "Oliver Sutton", "Ophelia Payne", "Oren Jellow", "Oscar Ruitt", "Otto B. Kilt", "Otto Carr", "Otto Graf", "Otto Whackew", "Owen Big", "Owen Cash", "Owen Money", "Owen Moore", "P. Brain", "Paige Turner", "Park A. Studebaker", "Parker Carr", "Pat Downe", "Pat Pending", "Patton Down DeHatches", "Pearl E White", "Pearl E. Gates", "Pearl E. Whites", "Peg Legge", "Penny Bunn", "Penny Lane", "Penny Nichols", "Penny Profit", "Penny Whistler", "Penny Wise", "Pepe C. Cola", "Pepe Roni", "Perry Mecium", "Pete Moss", "Pete Zaria", "Phil A. Delphia", "Phil A. Mignon", "Phil DeGrave", "Phil Graves", "Phil Rupp", "Phillip D. Bagg", "Polly Dent", "Polly Ester", "Quimby Ingmeen", "Quint S. Henschel", "R. M. Pitt", "Raney Schauer", "Ray Gunn", "Ray N. Carnation", "Ray Zenz", "Raynor Schein", "Reed Toomey", "Reid Enright", "Renee Sance", "Rex Easley", "Rex Karrs", "Rhea Curran", "Rhea Pollster", "Rhoda Booke", "Rhoda Mule", "Rich Feller", "Rich Guy", "Rich Kidd", "Rich Mann", "Rick Kleiner", "Rick O'Shea", "Rick Shaw", "Ricky T. Ladder", "Rip Tile", "Rip Torn", "Rita Booke", "Rita Story", "Rob A. Bank", "Rob Banks", "Robin Andis Merryman", "Robin Banks", "Robin DeCraydle", "Robin Meeblind", "Robin Money", "Rocky Beach", "Rocky Mountain", "Rocky Rhoades", "Rocky Shore", "Rod N. Reel", "Roger Overandout", "Roman Holiday", "Ron A. Muck", "Rory Storm", "Rosa Shore", "Rose Bush", "Rose Gardner", "Rosie Peach", "Rowan Boatman", "Royal Payne", "Rufus Leaking", "Russell Ingleaves", "Russell Sprout", "Rusty Blades", "Rusty Carr", "Rusty Dorr", "Rusty Fender", "Rusty Fossat", "Rusty Irons", "Rusty Keyes", "Rusty Nails", "Rusty Pipes", "Rusty Steele", "Ryan Carnation", "Ryan Coke", "Sal A. Mander", "Sal Ami", "Sal Minella", "Sal Sage", "Sally Forth", "Sally Mander", "Sam Dayoulpay", "Sam Manilla", "Sam Pull", "Sam Urai", "Samson Night", "Sandy Banks", "Sandy Beech", "Sandy C. Shore", "Sandy Spring", "Sarah Bellum", "Sarah Doctorinthehouse", "Sasha Klotz", "Sawyer B. Hind", "Scott Shawn DeRocks", "Seymour Legg", "Shanda Lear", "Shandy Lear", "Sharon A. Burger", "Sheri Cola", "Sherman Wadd Evver", "Shirley Knot", "Shirley U. Jest", "Sid Down", "Simon Swindells", "Sir Fin Waves", "Skip Dover", "Skip Roper", "Skip Stone", "Sonny Day", "Stan Dup", "Stan Still", "Stew Ng", "Stu Pitt", "Sue Case", "Sue Flay", "Sue Jeu", "Sue Permann", "Sue Render", "Sue Ridge", "Sue Shi", "Sue Yu", "Sy Burnette", "Tad Moore", "Tad Pohl", "Tamara Knight", "Tanya Hyde", "Tate Urtots", "Taylor Maid", "Ted E. Baer", "Telly Vision", "Terry Achey", "Terry Bull", "Theresa Brown", "Theresa Green", "Therese R. Green", "Thor Luther", "Tim Burr", "Tina See", "Tish Hughes", "Tom A. Toe", "Tom Katt", "Tom Katz", "Tom Morrow", "Tommy Gunn", "Tommy Hawk", "Trina Corder", "Trina Forest", "Trina Woods", "Ty Coon", "Ty Knotts", "Ty Malone", "Ty Tannick", "Ty Tass", "Tyrone Shoes", "U. O. Money", "U. P. Freehly", "Ulee Daway", "Val Crow", "Val Lay", "Val Veeta", "Vlad Tire", "Walt Smedley", "Walter Melon", "Wanda Rinn", "Warren Piece", "Warren T.", "Wayne Deer", "Will Power", "Will Wynn", "Willie Maykit", "Willie Waite", "Wilma Leggrowbach", "Winnie Bago", "Winnie Dipoo", "Winsom Cash", "Woody Forrest", "Woody U. No", "X. Benedict", "Xavier Breath", "Xavier Money", "Yule B. Sari", "Zeke N. Yeshallfind", "Zoe Mudgett Hertz", "Zoltan Pepper"]
""".trimIndent().fromJson<List<String>>()!!.toMutableList()
    }
}

fun getRandomName() = try {
    Names.names.randomRemove()
} catch (e: IndexOutOfBoundsException) {
    "Hello"
}