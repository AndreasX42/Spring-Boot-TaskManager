import { Box, Flex, Grid, GridItem } from "@chakra-ui/react";
import NavBar from "./components/NavBar";
import { useState } from "react";
import { Genre } from "./hooks/useGenres";
import PlatformSelector from "./components/PlatformSelector";
import { Platform } from "./hooks/useTodos";
import SortSelector from "./components/SortSelector";
import TodosHeading from "./components/TodoHeading";
import TodoGrid from "./components/TodoGrid";

export interface TodoQuery {
  userId: number;
  todoId: number;
  sortOrder: string;
  searchText: string;
}

function App() {
  const [todoQuery, setTodoQuery] = useState<TodoQuery>({} as TodoQuery);

  return (
    <Grid
      templateAreas={{
        base: '"nav" "main"',
        lg: '"nav nav" "aside main"',
      }}
      templateColumns={{
        base: "1fr",
        lg: "200px 1fr",
      }}
    >
      <GridItem area="nav">
        <NavBar
          onSearch={(searchText) => setTodoQuery({ ...todoQuery, searchText })}
        />
      </GridItem>

      {/* <Show above="lg">
        <GridItem area="aside" paddingX={5}>
          <GenreList
            onSelectGenre={(genre) => setGameQuery({ ...gameQuery, genre })}
            selectedGenre={gameQuery.genre}
          />
        </GridItem>
      </Show> */}

      <GridItem area="main">
        <Box paddingLeft={2}>
          <TodosHeading todoQuery={todoQuery} />
          <Flex marginBottom={5}>
            <Box marginRight={5}>
              <PlatformSelector
                onSelectedPlatform={(platform) =>
                  setGameQuery({ ...gameQuery, platform })
                }
                selectedPlatform={gameQuery.platform}
              />
            </Box>
            <SortSelector
              onSelectSortOrder={(sortOrder) =>
                setGameQuery({ ...gameQuery, sortOrder })
              }
              sortOrder={gameQuery.sortOrder}
            />
          </Flex>
        </Box>
        <TodoGrid todoQuery={todoQuery} />
      </GridItem>
    </Grid>
  );
}

export default App;
