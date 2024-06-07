module Graph where

import Data.List (intercalate)
import Set (Set)
import Set qualified as Set

class Graph g where
  empty :: g a
  vertex :: a -> g a
  union :: g a -> g a -> g a
  connect :: g a -> g a -> g a

data Relation a = Relation {domain :: Set a, relation :: Set (a, a)}
  deriving (Eq, Show)

data Basic a
  = Empty
  | Vertex a
  | Union (Basic a) (Basic a)
  | Connect (Basic a) (Basic a)

instance Graph Relation where
  empty = Relation Set.empty Set.empty
  vertex x = Relation (Set.singleton x) Set.empty
  union (Relation d1 r1) (Relation d2 r2) = Relation (Set.union d1 d2) (Set.union r1 r2)
  connect (Relation d1 r1) (Relation d2 r2) =
    Relation (Set.union d1 d2) (Set.union (Set.union r1 r2) (Set.fromList [(x, y) | x <- Set.toList d1, y <- Set.toList d2]))

dedupRelation :: (Ord a) => Relation a -> Relation a
dedupRelation (Relation d r) = Relation (Set.deduplicate d) (Set.deduplicate r)

instance (Ord a, Num a) => Num (Relation a) where
  fromInteger = dedupRelation . vertex . fromInteger
  (+) r1 r2 = dedupRelation (r1 `union` r2)
  (*) r1 r2 = dedupRelation (connect r1 r2)
  signum = const empty
  abs = id
  negate = id

instance Graph Basic where
  empty = Empty
  vertex = Vertex
  union = Union
  connect = Connect

instance (Ord a) => Eq (Basic a) where
  g1 == g2 = (fromBasic g1 :: Relation a) == (fromBasic g2 :: Relation a)

instance (Ord a, Num a) => Num (Basic a) where
  fromInteger = vertex . fromInteger
  (+) = union
  (*) = connect
  signum = const empty
  abs = id
  negate = id

instance Semigroup (Basic a) where
  (<>) = union

instance Monoid (Basic a) where
  mempty = Empty

fromBasic :: (Graph g) => Basic a -> g a
fromBasic Empty = empty
fromBasic (Vertex x) = vertex x
fromBasic (Union g1 g2) = fromBasic g1 `union` fromBasic g2
fromBasic (Connect g1 g2) = connect (fromBasic g1) (fromBasic g2)

instance (Ord a, Show a) => Show (Basic a) where
  show basic =
    let relationGraph = (fromBasic basic :: Relation a)
        allVerticesList = Set.toAscList $ domain relationGraph
        edgesList = Set.toAscList $ relation relationGraph
        formatEdge (x, y) = "(" ++ show x ++ "," ++ show y ++ ")"
        edgesString = "[" ++ intercalate "," (map formatEdge edgesList) ++ "]"
        isVertexInEdge ver = any (\(x, y) -> x == ver || y == ver) edgesList
        standaloneVertices = filter (not . isVertexInEdge) allVerticesList
        verticesString = "[" ++ intercalate "," (map show standaloneVertices) ++ "]"
     in "edges " ++ edgesString ++ " + vertices " ++ verticesString

-- | Example graph
-- >>> example34
-- edges [(1,2),(2,3),(2,4),(3,5),(4,5)] + vertices [17]
example34 :: Basic Int
example34 = 1 * 2 + 2 * (3 + 4) + (3 + 4) * 5 + 17

todot :: (Ord a, Show a) => Basic a -> String
todot basic =
  let relationGraph = fromBasic basic
      allVerticesList = Set.toAscList $ domain relationGraph
      edgesList = Set.toAscList $ relation relationGraph
      formatEdge (x, y) = show x ++ " -> " ++ show y ++ ";"
      formatVertex x = show x ++ ";"
      edgesString = unlines $ map formatEdge edgesList
      isVertexInEdge ver = any (\(x, y) -> x == ver || y == ver) edgesList
      standaloneVertices = filter (not . isVertexInEdge) allVerticesList
      verticesString = unlines $ map formatVertex standaloneVertices
   in "digraph {\n" ++ edgesString ++ verticesString ++ "}"

instance Functor Basic where
  fmap _ Empty = Empty
  fmap f (Vertex x) = Vertex $ f x
  fmap f (Union g1 g2) = Union (fmap f g1) (fmap f g2)
  fmap f (Connect g1 g2) = Connect (fmap f g1) (fmap f g2)

-- | Merge vertices
-- >>> mergeV 3 4 34 example34
-- edges [(1,2),(2,34),(34,5)] + vertices [17]
mergeV :: (Eq a) => a -> a -> a -> Basic a -> Basic a
mergeV a b c = fmap mergeVertex
  where
    mergeVertex x
      | x == a || x == b = c
      | otherwise = x

instance Applicative Basic where
  pure = Vertex
  Empty <*> _ = Empty
  _ <*> Empty = Empty
  Vertex f <*> Vertex a = Vertex (f a)
  Vertex f <*> Union a b = Union (fmap f a) (fmap f b)
  Vertex f <*> Connect a b = Connect (fmap f a) (fmap f b)
  Union f g <*> a = Union (f <*> a) (g <*> a)
  Connect f g <*> a = Connect (f <*> a) (g <*> a)

instance Monad Basic where
  return = pure
  Empty >>= _ = Empty
  Vertex a >>= f = f a
  Union a b >>= f = Union (a >>= f) (b >>= f)
  Connect a b >>= f = Connect (a >>= f) (b >>= f)

-- | Split Vertex
-- >>> splitV 34 3 4 (mergeV 3 4 34 example34)
-- edges [(1,2),(2,3),(2,4),(3,5),(4,5)] + vertices [17]
splitV :: (Eq a) => a -> a -> a -> Basic a -> Basic a
splitV a b c graph = graph >>= splitVertex
  where
    splitVertex x
      | x == a = return b `mappend` return c
      | otherwise = return x