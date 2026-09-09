function findSequence(input) {
  if (!Array.isArray(input) || input.length === 0) {
    return [];
  }

  const lengths = new Array(input.length).fill(1);
  const parents = new Array(input.length).fill(-1);
  let bestIndex = 0;

  for (let i = 0; i < input.length; i++) {
    for (let j = 0; j < i; j++) {
      if (input[j] < input[i] && lengths[j] + 1 >= lengths[i]) {
        lengths[i] = lengths[j] + 1;
        parents[i] = j;
      }
    }

    if (lengths[i] >= lengths[bestIndex]) {
      bestIndex = i;
    }
  }

  const result = [];
  let currentIndex = bestIndex;

  while (currentIndex !== -1) {
    result.push(input[currentIndex]);
    currentIndex = parents[currentIndex];
  }

  return result.reverse();
}
